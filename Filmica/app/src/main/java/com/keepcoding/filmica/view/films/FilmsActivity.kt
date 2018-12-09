package com.keepcoding.filmica.view.films

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.keepcoding.filmica.R
import com.keepcoding.filmica.data.Film
import com.keepcoding.filmica.view.detail.DetailsActivity
import com.keepcoding.filmica.view.detail.DetailsFragment
import com.keepcoding.filmica.view.search.SearchFragment
import com.keepcoding.filmica.view.trends.TrendsFragment
import com.keepcoding.filmica.view.watchlist.WatchlistFragment
import kotlinx.android.synthetic.main.activity_films.*
import java.util.*

const val TAG_FILMS = "films"
const val TAG_WATCHLIST = "watchlist"
const val TAG_SEARCH = "search"
const val TAG_TRENDS = "trends"

class FilmsActivity : AppCompatActivity(), FilmsFragment.OnItemClickListener, DetailsFragment.OnFilmSavedListener, TrendsFragment.OnItemClickListener,WatchlistFragment.OnItemClickListener, SearchFragment.OnItemClickListener {

    private lateinit var filmsFragment: FilmsFragment
    private lateinit var watchlistFragment: WatchlistFragment
    private lateinit var activeFragment: Fragment
    private lateinit var trendsFragment: TrendsFragment
    private lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

        if (savedInstanceState == null) {
            setupFragments()
        } else {
            val activeTag = savedInstanceState.getString("active", TAG_FILMS)
            restoreFragments(activeTag)
        }

        navigation?.setOnNavigationItemSelectedListener { item ->
            val id = item.itemId

            when (id) {
                R.id.action_discover -> showMainFragment(filmsFragment)
                R.id.action_watchlist -> showMainFragment(watchlistFragment)
                R.id.action_trends -> showMainFragment(trendsFragment)
                R.id.action_search -> showMainFragment(searchFragment)
            }

            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("active", activeFragment.tag)
    }

    private fun setupFragments() {
        filmsFragment = FilmsFragment()
        watchlistFragment = WatchlistFragment()
        trendsFragment = TrendsFragment()
        searchFragment = SearchFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.container_list, filmsFragment, TAG_FILMS)
            .add(R.id.container_list, watchlistFragment, TAG_WATCHLIST)
            .add(R.id.container_list, trendsFragment, TAG_TRENDS)
            .add(R.id.container_list, searchFragment, TAG_SEARCH)
            .hide(watchlistFragment)
            .hide(trendsFragment)
            .hide(searchFragment)
            .commit()

        activeFragment = filmsFragment
    }

    private fun restoreFragments(tag: String) {
        filmsFragment = supportFragmentManager.findFragmentByTag(TAG_FILMS) as FilmsFragment
        watchlistFragment = supportFragmentManager.findFragmentByTag(TAG_WATCHLIST) as WatchlistFragment
        trendsFragment = supportFragmentManager.findFragmentByTag(TAG_TRENDS) as TrendsFragment
        searchFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH) as SearchFragment


        activeFragment =
                if (tag == TAG_WATCHLIST)
                    watchlistFragment
                else if (tag == TAG_TRENDS)
                    trendsFragment
                else if (tag == TAG_SEARCH)
                     searchFragment
                else
                    filmsFragment
    }

    override fun onItemClicked(film: Film) {
        showDetails(film.id)
    }

    override fun onFilmSaved(film: Film) {
        watchlistFragment.loadWatchlist()
    }

    private fun showMainFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()

        activeFragment = fragment
    }

    fun showDetails(id: String) {
        if (isTablet())
            showDetailsFragment(id)
        else
            launchDetailsActivity(id)
    }

    private fun isTablet() = this.containerDetails != null


    private fun showDetailsFragment(id: String) {
        val detailsFragment = DetailsFragment.newInstance(id)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerDetails, detailsFragment)
            .commit()
    }

    private fun launchDetailsActivity(id: String) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

}