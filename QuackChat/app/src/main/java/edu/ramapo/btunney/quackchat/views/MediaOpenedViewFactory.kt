package edu.ramapo.btunney.quackchat.views

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import edu.ramapo.btunney.quackchat.R

/**
 * When a media message of type picture/video is opened, we want to change the Linear Layout to
 * show differently than when it's not opened
 *
 */
class MediaOpenedViewFactory {
    companion object {

        fun createOpenedMediaView(context: Context, mediaType: MessageViewType): ImageView {

            // Set height and width of opened media image
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.width = 50
            params.height = 50

            when (mediaType) {
                MessageViewType.PICTURE -> {
                    // TODO
                }
            }

            val openedImageView = ImageView(context)
            openedImageView.layoutParams = params

            openedImageView.setImageResource(R.drawable.ic_openedpicture)

//            // Add some padding to the view
//            val openedLinearLayout = LinearLayout(context)
//            openedLinearLayout.addView(openedImageView)
//            openedLinearLayout.setPadding(0, 20, 0, 20)
//
//            // Add to fragment layout
//            openedLinearLayout.addView(openedImageView)

            return openedImageView
        }
    }
}