package com.ultratechies.ghala.ui.warehouses


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.Warehouse

class WarehouseAdapter(listdata: ArrayList<Warehouse>) :
    RecyclerView.Adapter<WarehouseAdapter.MyHolder>() {
    var listdata: List<Warehouse> = listdata
    private var DURATION: Long = 200

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_warehouse, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val warehouseModel: Warehouse = listdata[position]

        if(warehouseModel != null){
            /**
             * Adapter animation
             */
            setAnimation(holder.itemView, position)
            /**
             * Set widget values
             */
            holder.warehouseName.text = warehouseModel.name
            holder.warehouseAddress.text = warehouseModel.location

            /**
             * Click listener on our card
             */
            holder.cardView.setOnClickListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Clicked on ${warehouseModel.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setAnimation(itemView: View, i: Int) {
        var i = i
        val on_attach = true
        if (!on_attach) {
            i = -1
        }
        val isNotFirstItem = i == -1
        i++
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay = if (isNotFirstItem) DURATION / 2 else i * DURATION / 3
        animator.duration = 500
        animatorSet.play(animator)
        animator.start()
    }

    override fun getItemCount(): Int {
        return listdata.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var warehouseName: TextView = itemView.findViewById(R.id.warehouseNameTV)
        var warehouseAddress: TextView = itemView.findViewById(R.id.warehouseAddressTV)
        var cardView: CardView = itemView.findViewById(R.id.card_view)

        init {
            warehouseAddress = itemView.findViewById(R.id.warehouseAddressTV)
        }
    }

}