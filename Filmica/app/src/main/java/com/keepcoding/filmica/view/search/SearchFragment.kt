package com.keepcoding.filmica.view.search


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView

import com.keepcoding.filmica.R
import com.keepcoding.filmica.data.Film
import com.keepcoding.filmica.data.FilmsRepo
import com.keepcoding.filmica.view.films.FilmsAdapter
import com.keepcoding.filmica.view.util.ItemOffsetDecoration
import kotlinx.android.synthetic.main.fragment_search.*

import kotlinx.android.synthetic.main.layout_error.*


class SearchFragment : Fragment() {

    lateinit var listener: OnItemClickListener
    var query: String = ""

    val list: RecyclerView by lazy {
        val instance = view!!.findViewById<RecyclerView>(R.id.listSearch)
        instance.addItemDecoration(ItemOffsetDecoration(R.dimen.offset_grid))
        instance.setHasFixedSize(true)
        instance
    }
    val adapter: FilmsAdapter by lazy {
        val instance = FilmsAdapter { film ->
            this.listener.onItemClicked(film)
        }

        instance
    }


    override fun onCreate(savedInstanceState: Bundle?) { //ok
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context?) { //OK
        super.onAttach(context)

        if (context is OnItemClickListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false) //ok
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)// ok

        list.adapter = adapter
        searchText.queryHint = "Search"
       // btnRetry?.setOnClickListener { reload() }

        btnRetry?.setOnClickListener { searchMovie(query) }

        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(newQuery: String?): Boolean {
                if(newQuery != null) {
                    searchMovie(newQuery)
                    query = newQuery
                    progressBar?.visibility = View.VISIBLE

                }

                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
          })
    }

    override fun onResume() {
        super.onResume()
        this.reload()
    }

    fun reload() {
            FilmsRepo.discoverSearch(query,context!!,
                { films ->
                    progressBar?.visibility = View.INVISIBLE
                    layoutError?.visibility = View.INVISIBLE
                    list.visibility = View.VISIBLE
                    adapter.setFilms(films)
                },
                { error ->
                    error.printStackTrace()
                    progressBar?.visibility = View.INVISIBLE
                    list.visibility = View.INVISIBLE
                    layoutError?.visibility = View.VISIBLE

                    error.printStackTrace()

                })
        }


        private fun searchMovie(query: String) {

            FilmsRepo.discoverSearch(query, context!!,
                { films ->
                    progressBar?.visibility = View.INVISIBLE
                    layoutError?.visibility = View.INVISIBLE
                    list.visibility = View.VISIBLE
                    adapter.setFilms(films)
                },
                { error ->
                    progressBar?.visibility = View.INVISIBLE
                    list.visibility = View.INVISIBLE
                    layoutError?.visibility = View.VISIBLE

                    error.printStackTrace()
                })

        }


        interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }
}

