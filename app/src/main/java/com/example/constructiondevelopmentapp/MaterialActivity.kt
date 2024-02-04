package com.example.constructiondevelopmentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MaterialActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<MaterialDetails>

    lateinit var imageId : Array<Int>
    lateinit var heading:Array<String>
    lateinit var news : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material)
        supportActionBar?.hide()
        imageId = arrayOf(
            R.drawable.green_concrete,
            R.drawable.carpets,
            R.drawable.bamboo,
            R.drawable.metal,
            R.drawable.solar,
            R.drawable.plastic_fencing,
            R.drawable.bottles,
            R.drawable.wood)

        heading= arrayOf(
            "Green Concrete",
            "Natural Fiber Carpets",
            "Bamboo Flooring",
            "Recycled Metal",
            "Solar Panels",
            "Recycled Plastic Fencing Posts",
            "Recycled Plastic Bottles",
            "Sustainable Wood"
        )

        news = arrayOf(
            getString(R.string.news_concrete),
            getString(R.string.news_carpet),
            getString(R.string.news_bamboo),
            getString(R.string.news_metal),
            getString(R.string.news_solar),
            getString(R.string.news_fencing),
            getString(R.string.news_bottle),
            getString(R.string.news_wood)

        )

        newRecyclerView=findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager= LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList= arrayListOf<MaterialDetails>()

        getUserdata()

    }

    private fun getUserdata() {
        for(i in imageId.indices){
            val news= MaterialDetails(imageId[i],heading[i])
            newArrayList.add(news)
        }

        val adapter=MyAdapter(newArrayList)

        val swipegesture = object : SwipeGesture(this){
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
        adapter.setOnItemClickListener(object:MyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                //Toast.makeText(this@MainActivity,"You clicked on item no. $position",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MaterialActivity,MaterialDetailsActivity::class.java)
                intent.putExtra("heading", newArrayList[position].heading)
                intent.putExtra("imageId",newArrayList[position].titleImage)
                intent.putExtra("news",news[position])
                startActivity(intent)
            }

        })
    }
}