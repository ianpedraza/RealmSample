package com.ianpedraza.realmsample.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ianpedraza.realmsample.data.Person
import com.ianpedraza.realmsample.databinding.ActivityMainBinding
import com.ianpedraza.realmsample.utils.viewBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.people.collect { people ->
                    showPeople(people)
                }
            }
        }
    }

    private fun showPeople(people: List<Person>) {
        binding.textViewOutput.text = null

        people.forEach {
            binding.textViewOutput.append("${it.name}\n")
        }
    }

    private fun setupUi() {
        binding.buttonAdd.setOnClickListener {
            viewModel.generatePerson()
        }
    }
}
