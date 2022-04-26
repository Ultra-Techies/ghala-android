package com.ultratechies.ghala.ui.warehouses


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.Warehouse

class WarehouseAdapter(listdata: ArrayList<Warehouse>, mfragment: Fragment) :
    RecyclerView.Adapter<WarehouseAdapter.MyHolder>() {
    var listdata: List<Warehouse> = listdata
    val mfragment = mfragment
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
                //do something
            }

            // Long click listener on our card
            holder.cardView.setOnLongClickListener(OnLongClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete ${warehouseModel.name}")
                    .setMessage("Are you sure you want to delete ${warehouseModel.name}?")
                    .setPositiveButton("Yes") { dialog, which ->
                        (mfragment as WarehousesFragment).deleteWarehouse(warehouseModel.id)
                    }
                    .setNegativeButton("No") { dialog, which ->
                        //do nothing
                    }
                    .show()
                return@OnLongClickListener true
            })
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