package com.rdxer.baserecyclerviewadapter.listviewadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * description
 *
 * @author Linsr
 */

public abstract class BaseListRecyclerViewAdapter<HM,
        CM> extends RecyclerView.Adapter<BaseListRecyclerViewAdapter.ViewHolder> {

    public static final int ID_HeaderView = 88888;
    public Context context;
    public LayoutInflater inflater;

    private OnItemClickListener<HM, CM> onItemClickListener;
    private OnHeaderClickListener<HM, CM> onHeaderClickListener;


    public abstract int getItemCountInSection(int section);
    public abstract <HVH extends HeaderViewHolder<HM>> HVH onCreateHeaderViewHolder(ViewGroup parent, int viewType);
    public abstract <CVH extends ChildViewHolder<CM>> CVH onCreateChileViewHolder(ViewGroup parent, int viewType);
    public abstract CM getChildModel(int section, int row);
    public abstract HM getHeaderModel(int section);

    public int getChildItemViewType(int section, int row){
        return 0;
    }

    public int getSectionCount(){
        return 1;
    }

    public BaseListRecyclerViewAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                sparseArray.clear();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ID_HeaderView){
            return onCreateHeaderViewHolder(parent,viewType);
        }
        return onCreateChileViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IndexPath indexPath = convert(position);
        Object model = null;
        if (indexPath.isSection()){
            model = getHeaderModel(indexPath.section);
            if (getOnHeaderClickListener() == null) {
                holder.itemView.setOnClickListener(null);
            }else{
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getOnHeaderClickListener() != null){
                            getOnHeaderClickListener().onHeaderClick(BaseListRecyclerViewAdapter.this,indexPath.section);
                        }
                    }
                });
            }

        }else{
            model = getChildModel(indexPath.section,indexPath.row);
            if (getOnItemClickListener()==null){
                holder.itemView.setOnClickListener(null);
            }else{
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getOnItemClickListener() != null) {
                            onItemClickListener.onItemClick(BaseListRecyclerViewAdapter.this,indexPath.section,indexPath.row);
                        }
                    }
                });
            }
        }
        holder.bindView(model);
    }

    SparseArray<IndexPath> sparseArray = new SparseArray<>();

    protected IndexPath convert(int position){
        IndexPath path = sparseArray.get(position);
        if (path != null){
            return path;
        }

        int surplus = position + 1;

        int sectionCount = getSectionCount();
        for (int section = 0; section < sectionCount; section++) {

            surplus -= 1;

            if (surplus == 0){
                path = new IndexPath(section,-1);
                sparseArray.put(position,path);
                return path;
            }

            int itemCount = getItemCountInSection(section);
            if (itemCount >= surplus){
                path = new IndexPath(section,surplus-1);
                sparseArray.put(position,path);
                return path;
            }
            surplus -= itemCount;
        }
        IndexPath indexPath = new IndexPath();
        indexPath.row = -1;
        sparseArray.put(position,indexPath);
        return indexPath;
    }

    @Override
    public int getItemViewType(int position) {
        IndexPath indexPath = convert(position);
        if (indexPath.isSection()){
            return ID_HeaderView;
        }else{
            return getChildItemViewType(indexPath.section,indexPath.row);
        }
    }

    @Override
    public int getItemCount() {
        int sectionCount = getSectionCount();
        int itemCount = sectionCount;
        for (int section = 0; section < sectionCount; section++) {
            itemCount += getItemCountInSection(section);
        }
        return itemCount;
    }

    public OnItemClickListener<HM,
            CM> getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<HM,
            CM> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnHeaderClickListener<HM, CM> getOnHeaderClickListener() {
        return onHeaderClickListener;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener<HM, CM> onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }


    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
            findView(itemView);
        }

        public abstract void findView(View itemView);
        public abstract void bindView(T model);
    }
    public static abstract class HeaderViewHolder<HM> extends ViewHolder<HM>{
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
    public static abstract class ChildViewHolder<M> extends ViewHolder<M>{
        public ChildViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class IndexPath{
        public int section;
        public int row;

        public boolean isSection() {
            return row == -1;
        }

        public IndexPath() {
        }

        public IndexPath(int row) {
            this.section = 0;
            this.row = row;
        }
        public IndexPath(int section, int row) {
            this.section = section;
            this.row = row;
        }
    }

    public interface OnItemClickListener<_HM,
            _CM> {
        void onItemClick(BaseListRecyclerViewAdapter<_HM,
                _CM> item, int section, int row);
    }
    public interface OnHeaderClickListener<_HM,
            _CM> {
        void onHeaderClick(BaseListRecyclerViewAdapter<_HM,
                _CM> item, int section);
    }

}
