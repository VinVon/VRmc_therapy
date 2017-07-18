package vr.promulgator.com.vrmc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vr.promulgator.com.vrmc.R;
import vr.promulgator.com.vrmc.adapter.abs.AbstractAdapter;
import vr.promulgator.com.vrmc.adapter.abs.ViewHolder;
import vr.promulgator.com.vrmc.bean.NophonePrescriptionContent;
import vr.promulgator.com.vrmc.bean.PrescriptionContent;

/**
 * Created by raytine on 2017/7/4.
 */

public class MyadapterNo extends AbstractAdapter<NophonePrescriptionContent.DataBean> {
   Callplat call;
    public MyadapterNo(Context context, List<NophonePrescriptionContent.DataBean> listData, int resId, Callplat call) {
        super(context, listData, resId);
        this.call = call;
    }

    @Override
    public void convert(ViewHolder holder, final NophonePrescriptionContent.DataBean bean) {
        TextView tv_name = holder.getView(R.id.mlist_name);
        TextView tv_time= holder.getView(R.id.mlist_time);
        TextView tv_num = holder.getView(R.id.mlist_num);
        ImageView tv_paly = holder.getView(R.id.mlist_paly);
        tv_name.setText(bean.getContent_name());
//        if (bean.getPeriodUnit() == 1){
//            tv_num.setText(bean.getFrequency()+"次/日-共"+bean.getPeriod()+"日-合计"+bean.getTimes()+"次  已使用"+bean.getUseTimes()+"次");
//        }else if(bean.getPeriodUnit() == 2){
//            tv_num.setText(bean.getFrequency()+"次/周-共"+bean.getPeriod()+"周-合计"+bean.getTimes()+"次  已使用"+bean.getUseTimes()+"次");
//        }else{
//            tv_num.setText(bean.getFrequency()+"次/月-共"+bean.getPeriod()+"月-合计"+bean.getTimes()+"次  已使用"+bean.getUseTimes()+"次");
//        }
        Picasso.with(context)
                .load(bean.getContent_coverPic())
                .into(tv_paly);
        tv_paly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.onsuccess(bean);
            }
        });
        tv_time.setVisibility(View.GONE);
    }
    public interface  Callplat{
        void onsuccess(NophonePrescriptionContent.DataBean id);
    };

}
