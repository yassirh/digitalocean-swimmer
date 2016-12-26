package com.yassirh.digitalocean.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.FloatingIP;
import com.yassirh.digitalocean.service.FloatingIPService;

import java.util.List;

public class FloatingIPFragment extends ListFragment implements Updatable, SwipeRefreshLayout.OnRefreshListener{

    private FloatingIPService floatingIPService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        floatingIPService = new FloatingIPService(this.getActivity());
        update(this.getActivity());
        View layout = inflater.inflate(R.layout.fragment_sizes, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_bright,
                R.color.green_light,
                R.color.orange_light,
                R.color.red_light);
        return layout;
    }

    @Override
    public void onRefresh() {
        floatingIPService.getAllFromAPI(true);
        handler.post(refreshing);
    }

    @Override
    public void update(Context context) {
        List<FloatingIP> ips = floatingIPService.getAll();
        FloatingIPAdapter floatingIPAdapter = new FloatingIPAdapter(this.getActivity(), ips);
        setListAdapter(floatingIPAdapter);
    }

    private final Runnable refreshing = new Runnable(){
        public void run(){
            try {
                if(floatingIPService.isRefreshing()){
                    handler.postDelayed(this, 1000);
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
