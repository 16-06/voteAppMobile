package com.example.voteapp.ui.vote.result

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.VoteDetailsDto
import com.example.voteapp.data.model.VoteOption
import com.example.voteapp.data.network.RetrofitInstance
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoteResultsFragment : Fragment() {

    private lateinit var chart : PieChart
    private lateinit var titleText : TextView
    private lateinit var authorText : TextView
    private lateinit var voteImage: ImageView
    private var voteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            voteId = it.getInt("voteId", -1)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vote_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = view.findViewById(R.id.chart)
        titleText = view.findViewById(R.id.textTitle)
        authorText = view.findViewById(R.id.textAuthor)
        voteImage = view.findViewById(R.id.voteImage)

        loadVoteDetails()
        loadVoteResults()

    }

    private fun loadVoteDetails() {
        val api = RetrofitInstance.getApi(requireContext())

        api.getVoteResult(voteId).enqueue(object : Callback<VoteDetailsDto> {

            override fun onResponse(call : Call<VoteDetailsDto>, response: Response<VoteDetailsDto>){
                if(response.isSuccessful) {
                    response.body()?.let { voteDetails ->

                        titleText.text = voteDetails.name
                        authorText.text = "Author: ${voteDetails.author}"
                        voteDetails.imageData?.let {
                            val decoded = Base64.decode(it, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                            voteImage.setImageBitmap(bitmap)

                    } }
                }
                else {
                        Toast.makeText(requireContext(), "Vote details not found", Toast.LENGTH_SHORT).show()
                    }
                }
            override fun onFailure(call: Call<VoteDetailsDto>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load vote details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadVoteResults() {
        val api = RetrofitInstance.getApi(requireContext())

        api.getVoteOptions(voteId).enqueue(object : Callback<List<VoteOption>>{
            override fun onResponse(call: Call<List<VoteOption>?>, response: Response<List<VoteOption>?>) {
                if(response.isSuccessful){
                    response.body()?.let { options ->
                        val entries = options.map { PieEntry(it.count.toFloat(), it.name) }
                        val dataSet = PieDataSet(entries, " - Vote Results")
                        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
                        val data = PieData(dataSet)
                        chart.data = data
                        chart.invalidate()
                    }
                }
            }

            override fun onFailure(call: Call<List<VoteOption>?>, t: Throwable) {
                Toast.makeText(requireContext(), "Error loading vote results", Toast.LENGTH_SHORT).show()
            }
        })
    }



}