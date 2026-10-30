package com.example.banglatranslator.data

import com.example.banglatranslator.model.NewsItem

object NewsRepository {

    val allNews: List<NewsItem> = listOf(
        NewsItem(
            id = 1,
            sourceUrl = "https://www.prothomalo.com",
            sourceName = "Prothom Alo",
            titleBn = "মেট্রোরেলের নতুন রুটের কাজ দ্রুত গতিতে এগিয়ে চলছে",
            titleEn = "Dhaka Metro Rail Expansion Proceeds at Rapid Pace on New Route",
            summaryBn = "রাজধানীর যানজট নিরসনে নতুন স্টেশনগুলোর আধুনিকায়ন এবং পরীক্ষামূলক চলাচল আগামী মাসেই শুরু হওয়ার ঘোষণা দেওয়া হয়েছে।",
            summaryEn = "Authorities announced trial runs and modernization for new stations starting next month to significantly ease capital traffic congestion.",
            publishedTimeAgo = "2h ago"
        ),
        NewsItem(
            id = 2,
            sourceUrl = "https://bdnews24.com",
            sourceName = "BDNews24",
            titleBn = "তথ্যপ্রযুক্তি খাতে বাংলাদেশি তরুণদের বৈশ্বিক সাফল্য বৃদ্ধি পাচ্ছে",
            titleEn = "Bangladeshi Tech Talents Make Greater Global Strides in Software & AI",
            summaryBn = "আন্তর্জাতিক শীর্ষ প্রযুক্তি প্রতিষ্ঠানগুলোতে দেশি প্রকৌশলীদের অবদান এবং দেশীয় স্টার্টআপে বিদেশি বিনিয়োগ নতুন রেকর্ড ছুঁয়েছে।",
            summaryEn = "Contributions from local software engineers at leading international tech firms and foreign startup investments have set record highs.",
            publishedTimeAgo = "5h ago"
        ),
        NewsItem(
            id = 3,
            sourceUrl = "https://www.dhakatribune.com",
            sourceName = "Dhaka Tribune",
            titleBn = "সুন্দরবনের জীববৈচিত্র্য সংরক্ষণে পরিবেশবান্ধব নতুন নীতিমালা কার্যকর",
            titleEn = "Eco-Friendly Conservation Policies Enacted to Safeguard Sundarbans Biodiversity",
            summaryBn = "ম্যানগ্রোভ বনের বেঙ্গল টাইগার ও বিপন্ন জলজ প্রাণীদের রক্ষায় ট্যুরিজম ও বাণিজ্যিক নৌ চলাচলে কঠোর নজরদারি আরোপ।",
            summaryEn = "Stricter surveillance on tourism and commercial vessel traffic takes effect to protect Bengal tigers and endangered aquatic wildlife in the mangrove delta.",
            publishedTimeAgo = "9h ago"
        ),
        NewsItem(
            id = 4,
            sourceUrl = "https://www.banglanews24.com",
            sourceName = "Banglanews24",
            titleBn = "চট্টগ্রাম সমুদ্রবন্দরে কনটেইনার হ্যান্ডলিংয়ে নতুন মাইলফলক অর্জন",
            titleEn = "Chattogram Seaport Sets New Operational Milestone in Container Cargo Handling",
            summaryBn = "আধুনিক টার্মিনাল চালু ও স্বয়ংক্রিয় কাস্টমস স্ক্যানিং সুবিধার কারণে পণ্য খালাসের সময় অর্ধেকে নেমে এসেছে।",
            summaryEn = "Automated customs scanning and expanded modern berths have cut cargo turnaround times in half, boosting regional trade efficiency.",
            publishedTimeAgo = "14h ago"
        ),
        NewsItem(
            id = 5,
            sourceUrl = "https://www.thedailystar.net",
            sourceName = "The Daily Star",
            titleBn = "কৃষি খাতে ন্যানো-প্রযুক্তির ব্যবহার: খাদ্য উৎপাদনে নতুন বিপ্লবের সম্ভাবনা",
            titleEn = "Nanotechnology in Agriculture: Promising Breakthroughs for Crop Yields in Bangladesh",
            summaryBn = "খরা ও লবণাক্ততা সহনশীল ধানের নতুন জাত উদ্ভাবনে গবেষকরা উল্লেখযোগ্য অগ্রগতি দাবি করেছেন।",
            summaryEn = "Researchers report significant progress in developing drought- and salinity-tolerant rice varieties suitable for changing climate conditions.",
            publishedTimeAgo = "1d ago"
        )
    )
}
