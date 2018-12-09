package com.keepcoding.filmica.data

import android.net.Uri
import com.keepcoding.filmica.BuildConfig

object ApiRoutes {


    //FILMS
    fun discoverUrl(
        language: String = "en-US",
        sort: String = "popularity.desc",
        page: Int = 1
    ): String {
        return getUriBuilder()
            .appendPath("discover")
            .appendPath("movie")
            .appendQueryParameter("language", language)
            .appendQueryParameter("sort_by", sort)
            .appendQueryParameter("page", page.toString())
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("include_video", "false")
            .build()
            .toString()
    }

  // TRENDS

    fun discoverUrlTrends(
        page: Int = 1,
        language: String = "en-US"

    ):
            String {
        return getUriBuilder()
            .appendPath("trending")
            .appendPath("movie")
            .appendPath("day")
            .build()
            .toString()
    }

    // SEARCH

    fun discoverUrlSearch(
        query: String,
        page: Int = 1,
        language: String = "en-US"

        ):  String {
        return getUriBuilder()
            .appendPath("search")
            .appendPath("movie")
            .appendQueryParameter("query", query)
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("language", language)
            .appendQueryParameter("include_video", "false")
            .appendQueryParameter("page", page.toString())
            .build()
            .toString()
    }

// Intro
    private fun getUriBuilder() =
        Uri.Builder()
            .scheme("https")
            .authority("api.themoviedb.org")
            .appendPath("3")
            .appendQueryParameter("api_key", BuildConfig.MovieDBApiKey)
}