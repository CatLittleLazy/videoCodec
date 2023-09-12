package com.youmehe.mediatry;

import android.content.Context;
import android.media.MediaCodecInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import static com.youmehe.mediatry.Utils.getAllCodecInfo;

/**
 * Created by youmehe on 5/12/21 22:17 PM description:
 */

class codecInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final Context mContext;

  private List<Object> supportCodec;

  public void setSupportCodec(List<Object> supportCodec) {
    this.supportCodec = supportCodec;
  }

  public codecInfoAdapter(Context mContext, List<Object> supportCodec) {
    this.mContext = mContext;
    this.supportCodec = supportCodec;
    if (supportCodec == null) {
      supportCodec = new ArrayList<>();
    }
  }

  @NonNull
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new NormalTextViewHolder(View.inflate(mContext, R.layout.item_text, null));
  }

  @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    String content =
        supportCodec.get(position) instanceof String ? (String) supportCodec.get(position) :
            ((MediaCodecInfo) supportCodec.get(position)).getName();
    ((NormalTextViewHolder) holder).mTextView.setText(content);
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public int getItemCount() {
    return supportCodec.size();
  }

  public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
    TextView mTextView;

    NormalTextViewHolder(View view) {
      super(view);
      mTextView = view.findViewById(R.id.info);
      mTextView.setOnClickListener(
          v -> {
            Log.d("NormalTextViewHolder", "onClick--> position = " + getLayoutPosition());
            if (getLayoutPosition() == 0) {
              //todo too bad imp
              int type = ((TextView) v).getText().toString().contains("ALL_CODECS") ? 0 : 1;
              codecInfoAdapter codecAdapter =
                  (codecInfoAdapter) ((RecyclerView) itemView.getParent()).getAdapter();
              if (codecAdapter != null) {
                codecAdapter.setSupportCodec(getAllCodecInfo(type));
                codecAdapter.notifyDataSetChanged();
              }
            }
          });
    }
  }
}
