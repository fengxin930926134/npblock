package com.np.block.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.np.block.R;
import com.np.block.activity.MainActivity;
import com.np.block.adapter.RankingRankAdapter;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 排行榜Fragment
 *
 * @author fengxin
 */
public class RankingRankFragment extends Fragment {

    /**
     * 父activity
     */
    private MainActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rank_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.ranking);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //适配器数据
        List<Users> usersList;
        if (CacheManager.getInstance().containsUsers(ConstUtils.CACHE_RANK_RANKING_MODE)) {
            usersList = CacheManager.getInstance().getUsers(ConstUtils.CACHE_RANK_RANKING_MODE);
        } else {
            usersList = new ArrayList<>();
        }
        // 设置adapter适配器
        RankingRankAdapter mAdapter = new RankingRankAdapter(R.layout.rank_item, usersList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (MainActivity) context;
    }
}
