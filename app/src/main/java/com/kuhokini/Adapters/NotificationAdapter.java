package com.kuhokini.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.kuhokini.Models.NotificationModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildNotificationBinding;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>{

    Context context;
    ArrayList<NotificationModel> models;
    Animation zoomAnimation;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> models) {
        this.context = context;
        this.models = models;
        zoomAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_in_out);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_notification, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel notificationModel = models.get(position);

        if (notificationModel.getSeen()){
            holder.binding.notificationBadge.setVisibility(View.GONE);
        }else {
            holder.binding.notificationBadge.setVisibility(View.VISIBLE);
            holder.binding.notificationBadge.startAnimation(zoomAnimation);
        }

        holder.itemView.setOnClickListener(v->{
            holder.binding.notificationBadge.setVisibility(View.GONE);
            Toast.makeText(context, "marked as read", Toast.LENGTH_SHORT).show();
        });

        String timeAgo = TimeAgo.using(notificationModel.getNotifyAt());
        String formattedText = "<b>From Kuhokini</b> • " + timeAgo + " • " + notificationModel.getType();
        CharSequence styledText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY);
        holder.binding.notificationType.setText(styledText);



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ChildNotificationBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildNotificationBinding.bind(itemView);
        }
    }
}
