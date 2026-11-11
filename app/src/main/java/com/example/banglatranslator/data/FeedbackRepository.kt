package com.example.banglatranslator.data

import android.content.Context
import android.content.SharedPreferences
import com.example.banglatranslator.model.FeedbackCategory
import com.example.banglatranslator.model.UserFeedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object FeedbackRepository {
    private const val PREFS_NAME = "bangla_translator_feedback_prefs"
    private const val KEY_FEEDBACK_ITEMS = "feedback_items"

    private val _feedbackList = MutableStateFlow<List<UserFeedback>>(emptyList())
    val feedbackList: StateFlow<List<UserFeedback>> = _feedbackList.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadFeedbacks()
        }
    }

    private fun loadFeedbacks() {
        val jsonString = prefs?.getString(KEY_FEEDBACK_ITEMS, null)
        if (jsonString.isNullOrEmpty()) {
            _feedbackList.value = emptyList()
            return
        }

        try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<UserFeedback>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val catString = obj.optString("category", FeedbackCategory.GENERAL.name)
                val category = try {
                    FeedbackCategory.valueOf(catString)
                } catch (e: Exception) {
                    FeedbackCategory.GENERAL
                }

                list.add(
                    UserFeedback(
                        id = obj.optString("id"),
                        rating = obj.optInt("rating", 5),
                        category = category,
                        feedbackText = obj.optString("feedbackText", ""),
                        sourceContext = if (obj.has("sourceContext") && !obj.isNull("sourceContext")) obj.getString("sourceContext") else null,
                        translationContext = if (obj.has("translationContext") && !obj.isNull("translationContext")) obj.getString("translationContext") else null,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            _feedbackList.value = list
        } catch (e: Exception) {
            _feedbackList.value = emptyList()
        }
    }

    fun submitFeedback(feedback: UserFeedback) {
        val current = _feedbackList.value.toMutableList()
        current.add(0, feedback)
        _feedbackList.value = current
        saveFeedbacks()
    }

    fun deleteFeedback(id: String) {
        val current = _feedbackList.value.filter { it.id != id }
        _feedbackList.value = current
        saveFeedbacks()
    }

    private fun saveFeedbacks() {
        try {
            val jsonArray = JSONArray()
            for (item in _feedbackList.value) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("rating", item.rating)
                    put("category", item.category.name)
                    put("feedbackText", item.feedbackText)
                    if (item.sourceContext != null) put("sourceContext", item.sourceContext)
                    if (item.translationContext != null) put("translationContext", item.translationContext)
                    put("timestamp", item.timestamp)
                }
                jsonArray.put(obj)
            }
            prefs?.edit()?.putString(KEY_FEEDBACK_ITEMS, jsonArray.toString())?.apply()
        } catch (e: Exception) {
            // Ignore write failures gracefully
        }
    }
}
