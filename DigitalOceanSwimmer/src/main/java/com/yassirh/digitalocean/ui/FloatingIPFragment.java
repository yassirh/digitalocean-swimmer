package com.yassirh.digitalocean.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.FloatingIP;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.FloatingIPService;

import java.util.List;

public class FloatingIPFragment extends ListFragment implements Updatable, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private FloatingIPService floatingIPService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingIP floatingIP;
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        floatingIP = new FloatingIPService(getActivity()).findById(info.id);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.floating_ip_context, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = getListView();
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(listView.getChildCount() > 0){
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        registerForContextMenu(listView);
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
