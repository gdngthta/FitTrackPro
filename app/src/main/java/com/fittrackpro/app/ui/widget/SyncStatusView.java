package com.fittrackpro.app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fittrackpro.app.R;

/**
 * SyncStatusView displays the current data synchronization status to the user.
 * Shows different states: synced, syncing, offline, or error.
 */
public class SyncStatusView extends LinearLayout {

    private ImageView iconSync;
    private TextView textStatus;

    public SyncStatusView(Context context) {
        super(context);
        init(context);
    }

    public SyncStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SyncStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_sync_status, this);
        iconSync = findViewById(R.id.iconSync);
        textStatus = findViewById(R.id.textStatus);
    }

    public void setStatus(SyncStatus status) {
        switch (status) {
            case SYNCED:
                iconSync.setImageResource(android.R.drawable.ic_menu_upload_you_tube); // Placeholder
                textStatus.setText(R.string.sync_status_synced);
                textStatus.setTextColor(getContext().getColor(android.R.color.holo_green_dark));
                setVisibility(View.VISIBLE);
                break;
            case SYNCING:
                iconSync.setImageResource(android.R.drawable.ic_popup_sync); // Placeholder
                textStatus.setText(R.string.sync_status_syncing);
                textStatus.setTextColor(getContext().getColor(android.R.color.darker_gray));
                setVisibility(View.VISIBLE);
                break;
            case OFFLINE:
                iconSync.setImageResource(android.R.drawable.ic_dialog_alert); // Placeholder
                textStatus.setText(R.string.sync_status_offline);
                textStatus.setTextColor(getContext().getColor(android.R.color.darker_gray));
                setVisibility(View.VISIBLE);
                break;
            case ERROR:
                iconSync.setImageResource(android.R.drawable.stat_notify_error); // Placeholder
                textStatus.setText(R.string.sync_status_error);
                textStatus.setTextColor(getContext().getColor(android.R.color.holo_red_dark));
                setVisibility(View.VISIBLE);
                break;
            case HIDDEN:
                setVisibility(View.GONE);
                break;
        }
    }

    public enum SyncStatus {
        SYNCED, SYNCING, OFFLINE, ERROR, HIDDEN
    }
}
