package white.ball.survival.present.dialog_screen.user_guide.adapter

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import white.ball.survival.R
import white.ball.survival.databinding.FragmentUserGuideDialogBinding
import white.ball.survival.domain.model.News.NewsUpDate
import white.ball.survival.domain.model.user_guide.DescriptionAnimal
import white.ball.survival.domain.model.user_guide.DescriptionPlant
import white.ball.survival.present.dialog_screen.user_guide.adapter.adapter.AnimalAdapter
import white.ball.survival.present.dialog_screen.user_guide.adapter.adapter.NewsUpDateAdapter
import white.ball.survival.present.dialog_screen.user_guide.adapter.adapter.PlantAdapter

class UserGuideDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentUserGuideDialogBinding

    private lateinit var animalAdapter: AnimalAdapter
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var newsUpDateAdapter: NewsUpDateAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(activity, R.layout.fragment_user_guide_dialog, null)
        binding = FragmentUserGuideDialogBinding.bind(view)
        val dialog = AlertDialog.Builder(activity)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        animalAdapter = AnimalAdapter()
        plantAdapter = PlantAdapter(requireContext())
        newsUpDateAdapter = NewsUpDateAdapter()

        animalAdapter.animalList = DescriptionAnimal.entries
        plantAdapter.plantList = DescriptionPlant.entries

        newsUpDateAdapter.newsUpDate = arrayListOf(
            NewsUpDate(
                captionResId = R.string.caption_1_1,
                dateResId = R.string.date_1_1,
                nameVersionResId = R.string.name_version_1_1,
                mainTextResId = R.string.main_text_1_1,
                imageResId = R.drawable.image_version_1_1
            )
        )

        with(binding) {
            sectionRecyclerview.layoutManager = linearLayoutManager

            sectionAnimalsTextView.setOnClickListener {
                if (!sectionRecyclerview.isVisible) {
                    sectionRecyclerview.adapter = animalAdapter
                    isShowSection(true)
                }
            }

            sectionPlantsTextView.setOnClickListener {
                if (!sectionRecyclerview.isVisible) {
                    sectionRecyclerview.adapter = plantAdapter
                    isShowSection(true)
                }
            }

            sectionNewsUpDateTextView.setOnClickListener {
                if (!sectionRecyclerview.isVisible) {
                    sectionRecyclerview.adapter = newsUpDateAdapter
                    isShowSection(true)
                }
            }

            iconBackImageButton.setOnClickListener {
                isShowSection(false)
            }

            exitTextView.setOnClickListener {
                dismiss()
            }

        }

        return dialog
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!

        with(window) {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun isShowSection(isShowSectionBoolean: Boolean) {
        with(binding) {
            if (isShowSectionBoolean) {
                sectionRecyclerview.isVisible = true
                iconBackImageButton.isVisible = true
                sectionAnimalsTextView.isVisible = false
                sectionPlantsTextView.isVisible = false
                sectionNewsUpDateTextView.isVisible = false
            } else {
                sectionRecyclerview.isVisible = false
                iconBackImageButton.isVisible = false
                sectionAnimalsTextView.isVisible = true
                sectionPlantsTextView.isVisible = true
                sectionNewsUpDateTextView.isVisible = true
            }
        }
    }

}