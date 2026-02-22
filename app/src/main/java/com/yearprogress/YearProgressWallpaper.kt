package com.yearprogress

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.app.Activity
import android.graphics.*
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.widget.*
import java.util.Calendar

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 180, 60, 60)
            setBackgroundColor(Color.parseColor("#080808"))
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        val title = TextView(this).apply {
            text = "Year Progress"
            textSize = 28f
            setTextColor(Color.parseColor("#ff6b35"))
            setPadding(0, 0, 0, 16)
            gravity = android.view.Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "Your year, one dot at a time."
            textSize = 14f
            setTextColor(Color.parseColor("#666666"))
            setPadding(0, 0, 0, 80)
            gravity = android.view.Gravity.CENTER
        }
        val btn = Button(this).apply {
            text = "Set as Live Wallpaper"
            textSize = 16f
            setBackgroundColor(Color.parseColor("#ff6b35"))
            setTextColor(Color.BLACK)
            setPadding(60, 40, 60, 40)
        }
        btn.setOnClickListener {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@MainActivity, YearProgressWallpaper::class.java))
            }
            startActivity(intent)
        }
        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(btn)
        setContentView(layout)
    }
}

class YearProgressWallpaper : WallpaperService() {
    override fun onCreateEngine(): Engine = YearProgressEngine()

    inner class YearProgressEngine : Engine() {
        private var width = 0
        private var height = 0
        private var lastDrawnDay = -1

        private val quotes = listOf(
            "Do not pray for an easy life. Pray for the strength to endure a difficult one." to "Bruce Lee",
            "Karm karo, phal ki chinta mat karo." to "Bhagavad Gita · Krishna to Arjuna",
            "Started from the bottom, now we're here." to "Drake",
            "Real eyes realize real lies." to "Tupac Shakur",
            "Be the change you wish to see in the world." to "Mahatma Gandhi",
            "Stay hungry, stay foolish." to "Steve Jobs",
            "Nobody built like you. You design yourself." to "Jay-Z",
            "Every moment is a fresh beginning." to "T.S. Eliot",
            "You are not a drop in the ocean. You are the entire ocean in a drop." to "Rumi",
            "I'd rather die enormous than live dormant." to "Jay-Z",
            "Doubt kills more dreams than failure ever will." to "Suzy Kassem",
            "Nanak naam chardi kala — In God's name, keep rising." to "Guru Nanak Dev Ji",
            "Hard times create strong men." to "G. Michael Hopf",
            "It always seems impossible until it's done." to "Nelson Mandela",
            "The soul is never born nor dies at any time." to "Bhagavad Gita · 2.20",
            "We are what we repeatedly do. Excellence is a habit." to "Aristotle",
            "Two things define you: patience when you have nothing, attitude when you have everything." to "Imam Ali",
            "Know yourself and you will win all battles." to "Sun Tzu",
            "If you're going through hell, keep going." to "Winston Churchill",
            "I am not what happened to me. I am what I choose to become." to "Carl Jung",
            "Fall seven times, stand up eight." to "Japanese Proverb",
            "Success is my only option — failure's not." to "Eminem · Lose Yourself",
            "Arise, awake, stop not till the goal is reached." to "Swami Vivekananda",
            "You miss 100% of the shots you don't take." to "Wayne Gretzky",
            "Even the darkest night will end and the sun will rise." to "Victor Hugo",
            "The best revenge is massive success." to "Frank Sinatra",
            "The wise see knowledge and action as one." to "Bhagavad Gita · 5.4",
            "Be fearless in the pursuit of what sets your soul on fire." to "Jennifer Lee",
            "I didn't come this far to only come this far." to "Unknown",
            "When you want to succeed as bad as you want to breathe, you will." to "Eric Thomas",
            "I am the master of my fate, the captain of my soul." to "William Ernest Henley",
            "Your life doesn't get better by chance. It gets better by change." to "Jim Rohn",
            "Legends don't die — I'm just getting started." to "Juice WRLD",
            "Don't be pushed by your problems. Be led by your dreams." to "Ralph Waldo Emerson",
            "Hard work beats talent when talent doesn't work hard." to "Tim Notke",
            "A smooth sea never made a skilled sailor." to "Franklin D. Roosevelt",
            "Believe you can and you're halfway there." to "Theodore Roosevelt",
            "Life is tough, my darling — but so are you." to "Stephanie Bennett-Henry",
            "The comeback is always stronger than the setback." to "Unknown",
            "Blessings keep falling in my lap." to "J. Cole · Love Yourz",
            "Nothing is permanent. Everything is transforming." to "Buddha",
            "The mind is everything. What you think, you become." to "Buddha",
            "Pain is inevitable. Suffering is optional." to "Haruki Murakami",
            "Stay low. Stay focused. Stay consistent." to "Unknown",
            "Don't count the days. Make the days count." to "Muhammad Ali",
            "He who conquers himself is the mightiest warrior." to "Confucius",
            "The lotus flower blooms most beautifully from the deepest mud." to "Buddhist saying",
            "How long will you wander? Come home to yourself." to "Rumi",
            "No pressure, no diamonds." to "Thomas Carlyle",
            "Work hard in silence. Let success make the noise." to "Frank Ocean",
            "Growth is uncomfortable — that's how you know it's working." to "Unknown",
            "Be like water — adapt, flow, find a way." to "Bruce Lee",
            "A king is not born — he is made through fire." to "Unknown",
            "You can't change the beginning but you can start now and change the ending." to "C.S. Lewis",
            "Everything is figureoutable." to "Marie Forleo",
            "Dream big. Work hard. Stay humble." to "Unknown",
            "Do something today your future self will thank you for." to "Unknown",
            "Stars can't shine without darkness." to "Unknown",
            "The cave you fear to enter holds the treasure you seek." to "Joseph Campbell",
            "Don't be the same — be better." to "Drake",
            "When the root is deep, there is no reason to fear the wind." to "African Proverb",
            "I am deliberate and afraid of nothing." to "Audre Lorde",
            "Work until your idols become your rivals." to "Unknown",
            "Some people want it to happen. Others make it happen." to "Michael Jordan",
            "Everything is hard before it's easy." to "Goethe",
            "I would rather fail daring greatly than succeed in mediocrity." to "Brené Brown",
            "You've survived 100% of your worst days. You're doing great." to "Unknown",
            "Be a warrior, not a worrier." to "Unknown",
            "Jab tak hai jaan — while there's life, there's hope." to "Indian Cinema Wisdom",
            "Never give in. Never, never, never, never." to "Winston Churchill",
            "Your story is not over yet." to "Unknown",
            "Every day is a second chance." to "Unknown",
            "Real ones know it never comes easy." to "J. Cole",
            "Keep the faith. The universe conspires for you." to "Paulo Coelho",
            "I am the storm." to "Unknown",
            "Make each day your masterpiece." to "John Wooden",
            "Live as if you were to die tomorrow. Learn as if you were to live forever." to "Mahatma Gandhi",
            "Today's pain is tomorrow's power." to "Unknown",
            "You define your own life. Don't let others write your script." to "Oprah Winfrey",
            "The gem cannot be polished without friction." to "Chinese Proverb",
            "You are the hero of your own story." to "Joseph Campbell",
            "The darkest hour has only sixty minutes." to "Morris Mandel",
            "Pressure makes diamonds." to "General George S. Patton",
            "Outwork your excuses." to "Unknown",
            "Be stubborn about your goals, flexible about your methods." to "Unknown",
            "You weren't born to just pay bills and die." to "Unknown",
            "The most powerful weapon on earth is the human soul on fire." to "Ferdinand Foch",
            "Mahaan bano — become great." to "Indian Wisdom",
            "Your breakthrough is coming. Hold on." to "Unknown",
            "You grow through what you go through." to "Tyrese Gibson",
            "The moment you give up is the moment you let someone else win." to "Kobe Bryant",
            "There's a lesson in every loss." to "Unknown",
            "Life rewards the brave." to "Tim Fargo",
            "Don't wait for opportunity. Create it." to "George Bernard Shaw",
            "The best is yet to come." to "Frank Sinatra",
            "You are closer than you think." to "Unknown",
            "One year from now, you'll wish you started today." to "Karen Lamb",
            "Sunrise is the universe saying: try again." to "Marty Rubin",
            "The greatest glory is not in never falling, but in rising every time." to "Confucius",
            "This is your year. Own it." to "Unknown",
            "Yoga is the journey of the self, through the self, to the self." to "Bhagavad Gita · 6.20",
            "Tujhe teri manzil milegi — you will find your destination." to "Indian Wisdom",
            "Sab theek ho jayega — trust the process." to "Indian Wisdom"
        )

        private fun isLeapYear(year: Int) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
        private fun getDayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        private fun getTotalDays(): Int = if (isLeapYear(Calendar.getInstance().get(Calendar.YEAR))) 366 else 365
        private fun getYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
            width = w; height = h; lastDrawnDay = -1; draw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) draw()
        }

        private fun draw() {
            val dayOfYear = getDayOfYear()
            if (dayOfYear == lastDrawnDay && width > 0) return
            lastDrawnDay = dayOfYear
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                canvas?.let { drawWallpaper(it, dayOfYear) }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }

        private fun drawWallpaper(canvas: Canvas, dayOfYear: Int) {
            val W = width.toFloat()
            val H = height.toFloat()
            val totalDays = getTotalDays()

            canvas.drawColor(Color.parseColor("#080808"))

            val yearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE; alpha = 10
                textSize = W * 0.32f; textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            }
            canvas.drawText(getYear().toString(), W / 2f, H * 0.13f, yearPaint)

            val COLS = 20
            val ROWS = Math.ceil(totalDays / COLS.toDouble()).toInt()
            val marginX = W * 0.07f
            val marginTop = H * 0.15f
            val gridW = W - marginX * 2f
            val gridH = H * 0.58f
            val cellW = gridW / COLS
            val cellH = gridH / ROWS
            val dotR = minOf(cellW, cellH) * 0.30f

            val pastPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#cccccc") }
            val futurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#191919") }
            val todayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#ff6b35") }
            val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#ff6b35"); alpha = 50 }
            val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 120 }

            for (i in 0 until totalDays) {
                val day = i + 1
                val col = i % COLS
                val row = i / COLS
                val cx = marginX + col * cellW + cellW / 2f
                val cy = marginTop + row * cellH + cellH / 2f
                when {
                    day < dayOfYear -> canvas.drawCircle(cx, cy, dotR, pastPaint)
                    day == dayOfYear -> {
                        canvas.drawCircle(cx, cy, dotR * 3.5f, glowPaint)
                        canvas.drawCircle(cx, cy, dotR, todayPaint)
                        canvas.drawCircle(cx - dotR * 0.25f, cy - dotR * 0.25f, dotR * 0.28f, highlightPaint)
                    }
                    else -> canvas.drawCircle(cx, cy, dotR, futurePaint)
                }
            }

            val divY = marginTop + gridH + H * 0.025f
            val linePaint = Paint().apply { color = Color.parseColor("#ff6b35"); alpha = 110; strokeWidth = 2f }
            canvas.drawLine(marginX, divY, W - marginX, divY, linePaint)

            val dayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#ff6b35"); textSize = W * 0.038f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }
            canvas.drawText("DAY $dayOfYear", W / 2f, divY + H * 0.048f, dayPaint)

            val remainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#444444"); textSize = W * 0.020f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }
            val pct = Math.round(dayOfYear.toFloat() / totalDays * 100)
            canvas.drawText("${totalDays - dayOfYear} days remaining  ·  $pct%", W / 2f, divY + H * 0.072f, remainPaint)

            val quoteIdx = (dayOfYear - 1) % quotes.size
            val (quoteText, quoteSource) = quotes[quoteIdx]

            val quotePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#d8d8d8"); textSize = W * 0.034f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            }
            val quoteY = divY + H * 0.105f
            val lineH = W * 0.042f
            val qLines = drawWrappedText(canvas, "\"$quoteText\"", W / 2f, quoteY, W * 0.80f, lineH, quotePaint)

            val sourcePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#ff6b35"); textSize = W * 0.024f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }
            canvas.drawText("— $quoteSource", W / 2f, quoteY + qLines * lineH + H * 0.022f, sourcePaint)
        }

        private fun drawWrappedText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, lineH: Float, paint: Paint): Int {
            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var line = ""
            for (word in words) {
                val test = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(test) > maxWidth && line.isNotEmpty()) { lines.add(line); line = word }
                else line = test
            }
            if (line.isNotEmpty()) lines.add(line)
            lines.forEachIndexed { i, l -> canvas.drawText(l, x, y + i * lineH, paint) }
            return lines.size
        }
    }
}
