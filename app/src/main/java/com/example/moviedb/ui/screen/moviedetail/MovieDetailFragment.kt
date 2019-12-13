package com.example.moviedb.ui.screen.moviedetail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentMovieDetailBinding
import com.example.moviedb.ui.base.BaseFragment
import com.example.moviedb.utils.setSingleClick
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailFragment : BaseFragment<FragmentMovieDetailBinding, MovieDetailViewModel>() {

    override val layoutId: Int = R.layout.fragment_movie_detail

    override val viewModel: MovieDetailViewModel by viewModel()

    private val args: MovieDetailFragmentArgs by navArgs()

    private val castAdapter = CastAdapter(itemClickListener = { imageView, cast ->
        cast.profile_path?.let { profilePath ->
            toFullImage(imageView, profilePath)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_favorite?.setOnClickListener {
            viewModel.favoriteMovie()
        }
        image_back?.setSingleClick {
            findNavController().navigateUp()
        }
        image_backdrop?.setSingleClick {
            viewModel.movie.value?.backdrop_path?.let {
                toFullImage(image_backdrop, it)
            }
        }

        viewModel.apply {
            args.movie.let {
                movie.value = it
                checkFavorite(it.id)
                loadCastAndCrew(it.id)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    image_backdrop?.transitionName = it.backdrop_path
                }
            }
            recycler_cast?.adapter = castAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.apply {
            cast.observe(viewLifecycleOwner, Observer {
                castAdapter.submitList(it)
            })
        }
    }

    private fun toFullImage(imageView: ImageView, imageUrl: String) {
        findNavController().navigate(
            MovieDetailFragmentDirections.toImage(imageUrl),
            FragmentNavigatorExtras(
                imageView to imageUrl
            )
        )
    }
}