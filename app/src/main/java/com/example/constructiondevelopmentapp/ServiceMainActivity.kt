package com.example.constructiondevelopmentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ServiceMainActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<ServiceModel>

    lateinit var imageId : Array<Int>
    lateinit var heading:Array<String>
    lateinit var news : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_main)
        supportActionBar?.hide()
        imageId = arrayOf(
            R.drawable.enegy_efficiency,
            R.drawable.rainwater,
            R.drawable.waste,
            R.drawable.leed,
            R.drawable.air)


        heading= arrayOf(
            "Energy Efficiency",
            "Rain Water Harvesting",
            "Waste Reduction",
            "LEED Certification",
            " Indoor Air Quality"
        )

        news = arrayOf(
            getString(R.string.news_energy),
            getString(R.string.news_rainwater),
            getString(R.string.news_waste),
            getString(R.string.news_leed),
            getString(R.string.news_air)

            )


        newRecyclerView=findViewById(R.id.recyclerViewService)
        newRecyclerView.layoutManager= LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList= arrayListOf<ServiceModel>()

        getUserdata()

    }



    private fun getUserdata() {
        for(i in imageId.indices){
            val news= ServiceModel(imageId[i],heading[i])
            newArrayList.add(news)
        }


        val adapter=ServiceAdapter(newArrayList)

        val swipegesture = object : SwipeGestureService(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when(direction){

                    //ItemTouchHelper.LEFT->{

                    //    adapter.deleteItem(viewHolder.adapterPosition)

                    //}


                    ItemTouchHelper.RIGHT->{
                        val archiveItem = newArrayList[viewHolder.adapterPosition]
                        adapter.deleteItem(viewHolder.adapterPosition)
                        adapter.addItem(newArrayList.size,archiveItem)
                    }

                }


            }
        }


        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(newRecyclerView)




        newRecyclerView.adapter=adapter
        adapter.setOnItemClickListener(object:ServiceAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //Toast.makeText(this@MainActivity,"You clicked on item no. $position",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ServiceMainActivity,ServiceActivity::class.java)
                intent.putExtra("heading", newArrayList[position].heading1)
                intent.putExtra("imageId",newArrayList[position].titleImage1)
                intent.putExtra("news",news[position])
                startActivity(intent)
            }

        })
    }
}