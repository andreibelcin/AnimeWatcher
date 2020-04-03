package com.animewatcher

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = this::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
            val animeId: Int
            val aniListQueryDispatcher = AniListQueryDispatcher()
            try {
                animeId = editText.text.toString().toInt()
                aniListQueryDispatcher.dispatch(
                    ExampleQuery.builder().id(animeId).build(),
                    responseHandler = {
                        Log.i(TAG, it.data().toString())
                        this.runOnUiThread {
                            Picasso.get().load(it.data()?.Media()?.coverImage()?.large())
                                .into(imageView)
                            textView.text = it.data()?.Media()?.title()?.romaji()
                        }
                    },
                    errorHandler = {
                        Log.e(TAG, it.message, it)
                        this.runOnUiThread {
                            imageView.setImageDrawable(
                                resources.getDrawable(
                                    R.drawable.ic_launcher_background,
                                    null
                                )
                            )
                            textView.text = getString(R.string.no_anime_was_found_exception)
                        }
                    }
                )
            } catch (e: NumberFormatException) {
                Log.e(TAG, e.message, e)
                this.runOnUiThread {
                    imageView.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_launcher_background,
                            null
                        )
                    )
                    textView.text = getString(R.string.invalid_anime_id_exception)
                }
            }
        }
    }
}
