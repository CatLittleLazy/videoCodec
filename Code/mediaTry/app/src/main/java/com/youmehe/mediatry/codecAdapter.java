package com.youmehe.mediatry;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.youmehe.mediatry.Utils.getAllCodec;

/**
 * Created by youmehe on 5/11/21 11:31 PM description:
 */

class codecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final Context mContext;

  private List<String> supportCodec;

  public void setSupportCodec(List<String> supportCodec) {
    this.supportCodec = supportCodec;
  }

  public codecAdapter(Context mContext, List<String> supportCodec) {
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
    ((NormalTextViewHolder) holder).mTextView.setText(supportCodec.get(position));
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
              codecAdapter codecAdapter =
                  (com.youmehe.mediatry.codecAdapter) ((RecyclerView) itemView.getParent()).getAdapter();
              if (codecAdapter != null) {
                try {
                  codecAdapter.setSupportCodec(getAllCodec(type));
                } catch (IOException e) {
                  e.printStackTrace();
                }
                codecAdapter.notifyDataSetChanged();
              }
            }
          });
    }
  }
}
