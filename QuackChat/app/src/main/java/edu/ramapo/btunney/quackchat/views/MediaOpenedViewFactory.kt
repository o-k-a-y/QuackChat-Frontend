package edu.ramapo.btunney.quackchat.views

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import edu.ramapo.btunney.quackchat.R

/**
 * This factory creates LinearLayouts representing a picture/video.
 * When a media message of type picture/video is opened, we want to change the LinearLayout to
 * show differently than when it's not opened (filled in red/purple square vs empty square)
 *
 */
class MediaOpenedViewFactory {
    companion object {
        fun createOpenedMediaView(context: Context, mediaType: MessageViewType): LinearLayout {
            val openedImageLinearLayout = LinearLayout(context)
            openedImageLinearLayout.setPadding(20, 0, 20, 0)

            val openedImageView = ImageView(context)

            // Set height and width of opened media image
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.width = 50
            params.height = 50
            openedImageView.layoutParams = params

            // The image will be red border if picture, purple border if video
            when (mediaType) {
                MessageViewType.PICTURE -> {
                    openedImageView.setImageResource(R.drawable.ic_openedpicture)
                }
                MessageViewType.VIDEO -> {
                    openedImageView.setImageResource(R.drawable.ic_openedvideo)
                }
                else -> {
                    // TODO: any other types
                }
            }

            openedImageLinearLayout.addView(openedImageView)
            return openedImageLinearLayout
        }
    }
}