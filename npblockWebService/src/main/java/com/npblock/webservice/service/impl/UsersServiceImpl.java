package com.npblock.webservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.ClassicRecord;
import com.npblock.webservice.entity.RankRecord;
import com.npblock.webservice.entity.RushRecord;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.repository.ClassicRecordRepository;
import com.npblock.webservice.repository.RankRecordRepository;
import com.npblock.webservice.repository.RushRecordRepository;
import com.npblock.webservice.repository.UsersRepository;
import com.npblock.webservice.service.UsersService;
import com.npblock.webservice.util.ConstUtils;
import com.npblock.webservice.util.HttpUtils;
import com.npblock.webservice.util.StringUtils;
import com.npblock.webservice.util.TokenUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final @NonNull UsersRepository usersRepository;
    private final @NonNull ClassicRecordRepository classicRecordRepository;
    private final @NonNull RushRecordRepository rushRecordRepository;
    private final @NonNull RankRecordRepository rankRecordRepository;

    @Override
    public Users loginByPhoneAndPassword(String phone, String password) {
        Users users = usersRepository.findFirstByPhoneAndPassword(phone, password);
        if (users != null) {
            //刷新token时间
            long time = TokenUtils.refreshTokenTime();
            if (usersRepository.updateTokenTimeById(users.getId(), time) > 0) {
                users.setTokenTime(time);
                return users;
            } else {
                return new Users();
            }
        }
        // 帐号密码错误
        return null;
    }

    @Override
    public Users register(Users users) {
        return registerPhoneOrQQ(users);
    }

    @Override
    public Users loginByQQ(Users users) {
        Users user = usersRepository.findByOpenId(users.getOpenId());
        if (user != null){
            //刷新token时间
            long time = TokenUtils.refreshTokenTime();
            if (usersRepository.updateTokenTimeById(user.getId(), time) > 0) {
                user.setTokenTime(time);
                return user;
            }
        } else {
            // 转换头像url格式
            String headSculpture = users.getHeadSculpture();
            if (org.apache.commons.lang.StringUtils.isNotBlank(headSculpture)) {
                users.setHeadSculpture(StringUtils.HttpToHttps(headSculpture));
            }
            //qq注册
            return registerPhoneOrQQ(users);
        }
        return null;
    }

    /**
     * 核心注册方法
     *
     * @param users 用户信息
     * @return users
     */
    private Users registerPhoneOrQQ(Users users) {
        //默认男
        users.setSex(1);
        users.setWalletBlock(0);
        users.setWalletJewel(0);
        users.setToken(TokenUtils.generateToken());
        users.setTokenTime(TokenUtils.refreshTokenTime());
        users.setCreateDate(new Date());
        users = usersRepository.save(users);
        if (users != null) {
//            //保存成功 注册环信
//            JSONObject body = new JSONObject();
//            body.put("username", users.getId());
//            body.put("password", users.getToken());
//            body.put("nickname", users.getName());
//            if (HttpUtils.http(ConstUtils.IM_URL + "/users", body, true, HttpMethod.POST) == null){
//                // 一般不会失败，失败怎么办，手动回滚...
//                usersRepository.delete(users);
//                return null;
//            } else {
//                //创建用户排行榜记录
//                createRankRecord(users);
//            }
//            return users;
            //创建用户排行榜记录
            createRankRecord(users);
        }
        return null;
    }

    /**
     * 创建用户排行榜记录
     */
    private void createRankRecord(Users users) {
        RankRecord rankRecord = rankRecordRepository.save(RankRecord.getDefaultRankRecord());
        RushRecord rushRecord = rushRecordRepository.save(RushRecord.getDefaultRushRecord());
        ClassicRecord classicRecord = classicRecordRepository.save(ClassicRecord.getDefaultClassicRecord());
        users.setClassicId(classicRecord.getId());
        users.setRankId(rankRecord.getId());
        users.setRushId(rushRecord.getId());
        usersRepository.save(users);
    }

    @Override
    public Users loginByToken(String token, long tokenTime) {
        //判断是否过期
        if (TokenUtils.tokenIsNotExpired(tokenTime)){
            Users user = usersRepository.findByToken(token);
            if (user != null && user.getTokenTime() == tokenTime) {
                //刷新token时间
                long time = TokenUtils.refreshTokenTime();
                if (usersRepository.updateTokenTimeById(user.getId(), time) > 0) {
                    user.setTokenTime(time);
                    user.setPassword(null);
                    return user;
                } else {
                    return new Users();
                }
            }
        }
        return null;
    }

    @Override
    public boolean expiredPhone(String phone) {
        Users user = usersRepository.findByPhone(phone);
        return user == null;
    }

    @Override
    public boolean expiredGameName(String gameName) {
        return usersRepository.findUsersByGameName(gameName) != null;
    }

    @Override
    public boolean updateGameName(String token, String gameName) {
        return usersRepository.updateGameNameByToken(token, gameName) > 0;
    }

    @Override
    public boolean updateBlockById(Integer id, Integer block) {
        return usersRepository.updateBlockById(id, block) > 0;
    }

    @Override
    public List<Users> selectUserBySexAndName(String name, Integer sex, String token) {
        Users my = usersRepository.findByToken(token);
        //判断token是否过期
        if (!TokenUtils.tokenIsNotExpired(my.getTokenTime())) {
            return null;
        }
        if (name != null) {
            name = "%" + name + "%";
        } else {
            name = "%%";
        }
        if (sex == null) {
            sex = 1;
        }
        //定义排序规则
        Sort sort = new Sort(Sort.Direction.DESC, "tokenTime");
        //创建分页对象
        Pageable pageable = PageRequest.of(0, ConstUtils.SELECT_USER_NUMBER, sort);
        Page<Users> page = usersRepository.findByGameNameLikeAndSex(pageable, name, sex);
        List<Users> content = page.getContent();
        //消除不需要的
        int index = -1;
        for (int i = 0; i < content.size(); i++) {
            Users user = content.get(i);
            user.setPassword("");
            //除掉自己
            if (user.getToken().equals(my.getToken())) {
                index = i;
            }
        }
        if (index != -1) {
            List<Users> users = new ArrayList<>(content);
            users.remove(index);
            return users;
        }
        return content;
    }

    @Override
    public List<Users> selectFriendById(int id) {
        JSONObject http = HttpUtils.http(ConstUtils.IM_URL.concat("/users/" + id + "/contacts/users"), null, true, HttpMethod.GET);
        if (http != null) {
            JSONArray data = http.getJSONArray("data");
            List<Integer> integers = data.toJavaList(Integer.class);
            if (integers != null && integers.size() != 0) {
                List<Users> users = usersRepository.findByIdIn(integers);
                for (Users user: users) {
                    user.setPassword("");
                }
                return users;
            }
        }
        return null;
    }

    @Override
    public List<Users> findByIdIn(List<Integer> ids) {
        List<Users> usersList = usersRepository.findByIdIn(ids);
        if (usersList != null) {
            for (Users users : usersList) {
                users.setPassword("");
            }
            return usersList;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean tokenIsExpired(String token) {
        Users byToken = usersRepository.findByToken(token);
        if (byToken != null && byToken.getTokenTime() != null) {
            //判断token是否有效
            return !TokenUtils.tokenIsNotExpired(byToken.getTokenTime());
        }
        return true;
    }
}
