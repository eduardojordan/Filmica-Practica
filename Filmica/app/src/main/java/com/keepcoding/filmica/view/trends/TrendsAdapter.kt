package com.keepcoding.filmica.view.trends

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.View
import com.keepcoding.filmica.R
import com.keepcoding.filmica.data.Film
import com.keepcoding.filmica.view.util.BaseFilmAdapter
import com.keepcoding.filmica.view.util.BaseFilmHolder
import com.keepcoding.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_film.view.*



class TrendsAdapter (itemClickListener: ((Film) -> Unit)? = null) :
    BaseFilmAdapter<TrendsAdapter.FilmViewHolder>(
        layoutItem = R.layout.item_film,
        holderCreator = { view -> FilmViewHolder(view, itemClickListener) }
    ) {


    class FilmViewHolder(
        view: View,
        listener: ((Film) -> Unit)? = null
    ) : BaseFilmHolder(view, listener) {

        override fun bindFilm(film: Film) {
            super.bindFilm(film)

            with(itemView) {
                labelTitle.text = film.title
                titleGenre.text = film.genre
                labelVotes.text = film.voteRating.toString()
                loadImage()
            }
        }

        private fun loadImage() {
            val target = SimpleTarget(
                successCallback = { bitmap, from ->
                    itemView.imgPoster.setImageBitmap(bitmap)
                    setColorFrom(bitmap)
                }
            )

            itemView.imgPoster.tag = target

            Picasso.get()
                .load(film.getPosterUrl())
                .error(R.drawable.placeholder)
                .into(target)
        }

        private fun setColorFrom(bitmap: Bitmap) {
            Palette.from(bitmap).generate { palette ->
                val defaultColor = ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch
                val color = swatch?.rgb ?: defaultColor

                itemView.container.setBackgroundColor(color)
                itemView.containerData.setBackgroundColor(color)
            }
        }
    }
}
