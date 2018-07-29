package com.spyrakis.calculator.adapters

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.spyrakis.calculator.R
import kotlinx.android.synthetic.main.list_item.view.*

/**
Created by pspyrakis on 29/7/18.
 */
class CustomSpinnerAdapter(context: Context, objects: Array<String>) : ArrayAdapter<String>(context, R.layout.list_item, objects), ThemedSpinnerAdapter {
    private val mDropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View

        view = if (convertView == null) {
            // Inflate the drop down using the helper's LayoutInflater
            val inflater = mDropDownHelper.dropDownViewInflater
            inflater.inflate(R.layout.list_item, parent, false)
        } else {
            convertView
        }

        view.text1.text = getItem(position)

        return view
    }

    override fun getDropDownViewTheme(): Resources.Theme? {
        return mDropDownHelper.dropDownViewTheme
    }

    override fun setDropDownViewTheme(theme: Resources.Theme?) {
        mDropDownHelper.dropDownViewTheme = theme
    }
}