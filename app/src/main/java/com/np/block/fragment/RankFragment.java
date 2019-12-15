package com.np.block.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.np.block.R;
import com.np.block.activity.MainActivity;
import com.np.block.adapter.ClassicRankAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * 排行榜Fragment
 *
 * @author fengxin
 */
public class RankFragment extends Fragment {

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
        // 设置adapter
        ClassicRankAdapter mAdapter = new ClassicRankAdapter(R.layout.rank_item);
        parentActivity.adapterList.add(mAdapter);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (MainActivity) context;
    }
}
