package com.ch96.tpwhereis.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바를 제목줄로 대체 - 옵션메뉴를 가질 수 있도록
        setSupportActionBar(binding.toolbar)

    }

    //옵션메뉴 액션바에붙이기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_aa -> Toast.makeText(this, "aa", Toast.LENGTH_SHORT).show()
            R.id.menu_bb -> Toast.makeText(this, "bb", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}