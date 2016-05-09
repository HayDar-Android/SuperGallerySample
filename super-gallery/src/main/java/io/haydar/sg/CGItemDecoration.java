package io.haydar.sg;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gjy on 16/4/27.
 */
public class CGItemDecoration extends RecyclerView.ItemDecoration {

    private final int SPACE = 10;

    public CGItemDecoration() {
        super();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if(position<3){
            outRect.top=SPACE;
        }
        if ((position + 1) % 3 == 0) {
            outRect.right = 0;
            outRect.bottom = SPACE;
            return;
        }
        outRect.right = SPACE;
        outRect.bottom = SPACE;
    }
}
