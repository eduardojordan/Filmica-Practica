package com.keepcoding.filmica.data

import android.arch.persistence.room.Room
import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


object FilmsRepo {

    private val films: MutableList<Film> = mutableListOf()
    private val trends: MutableList<Film> = mutableListOf()
    private val search: MutableList<Film> = mutableListOf()

    @Volatile
    private var db: AppDatabase? = null

    private fun getDbInstance(context: Context): AppDatabase {
        if (db == null) {

            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "filmica-db"
            ).build()
        }

        return db as AppDatabase
    }

    fun findFilmById(id: String): Film? {

        val filmInDiscover = films.find { film -> film.id == id }
        if (filmInDiscover != null) {
            return filmInDiscover
        }

        val filmInTrending = trends.find { film -> film.id == id }
        if (filmInTrending != null) {
            return filmInTrending
        }

        val filmInSearch = search.find { film -> film.id == id }
        if (filmInSearch != null) {
            return filmInSearch
        }

        return null

    }

    // FILM
    fun discoverFilms(
        context: Context,
        callbackSuccess: ((MutableList<Film>) -> Unit),
        callbackError: ((VolleyError) -> Unit)
    ) {

        if (films.isEmpty()) {
            requestDiscoverFilms(callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(films)
        }
    }

    private fun requestDiscoverFilms(
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverUrl()
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val newFilms = Film.parseFilms(response)
                films.addAll(newFilms)
                callbackSuccess.invoke(films)
            },
            { error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

    //TRENDS

    fun discoverTrends(context: Context,
                       callbackSuccess: ((MutableList<Film>) -> Unit),
                       callbackError: ((VolleyError) -> Unit)){

        if (trends.isEmpty()){
            requestDiscoverTrends(callbackSuccess, callbackError, context)
        }else{
            callbackSuccess.invoke(trends)
        }

    }

    private fun requestDiscoverTrends(
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverUrlTrends()
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                val newFilms = Film.parseFilms(response)
            trends.addAll(newFilms)
            callbackSuccess.invoke(trends)
        },
            { error ->
            callbackError.invoke(error)
        })
        Volley.newRequestQueue(context)
            .add(request)
    }

// SEARCH

    fun discoverSearch(query:String,context: Context,
                       callbackSuccess: ((MutableList<Film>) -> Unit),
                       callbackError: ((VolleyError) -> Unit))
    {

        search.clear()
            requestDiscoverSearch(query, callbackSuccess, callbackError, context)
       // }else{
       //     callbackSuccess.invoke(search)


    }

    private fun requestDiscoverSearch( query: String,
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverUrlSearch(query)
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                val newFilms = Film.parseFilms(response)
                search.addAll(newFilms)
                callbackSuccess.invoke(search)
            },
            { error ->
                callbackError.invoke(error)
            })
        Volley.newRequestQueue(context)
            .add(request)
    }

    fun saveFilm(
        context: Context,
        film: Film,
        callbackSuccess: (Film) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().insertFilm(film)
            }

            async.await()
            callbackSuccess.invoke(film)
        }
    }

    fun watchlist(
        context: Context,
        callbackSuccess: (List<Film>) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().getFilms()
            }

            val films: List<Film> = async.await()
            callbackSuccess.invoke(films)
        }
    }

    fun deleteFilm(
        context: Context,
        film: Film,
        callbackSuccess: (Film) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().deleteFilm(film)
            }
            async.await()
            callbackSuccess.invoke(film)
        }
    }


}