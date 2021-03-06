package com.nickolay.android2ver2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nickolay.android2ver2.main.CitySelect
import com.nickolay.android2ver2.main.CityWeather
import com.nickolay.android2ver2.model.GlobalViewModel
import com.nickolay.android2ver2.service.CommonWeather
import com.nickolay.delme.BatteryReceiver
import com.nickolay.delme.ConnectivityReceiver
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // Используется, чтобы определить результат активити регистрации через Гугл
    private val RC_SIGN_IN = 40404

    // Клиент для регистрации пользователя через Гугл
    private lateinit var googleSignInClient: GoogleSignInClient

    private  var  batteryReceiver: BroadcastReceiver? = BatteryReceiver()
    private  var  connectivityReceiver: BroadcastReceiver? = ConnectivityReceiver()

    private val PREFS_KEY_THEME = "theme"
    private val THEME_LIGHT = 0
    private val THEME_DARK = 1

    private var isDark = false
    private val sharedPrefs by lazy {
        getSharedPreferences(
            (MainActivity::class).qualifiedName,
            Context.MODE_PRIVATE
        )
    }

    private fun saveKey(key: String, theme: Int) = sharedPrefs.edit().putInt(key, theme).apply()
    private fun loadKey(key: String) = sharedPrefs.getInt(key, 0)

//    private var currentFabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
    private lateinit var viewModel: GlobalViewModel
    private lateinit var addVisibilityChanged: FloatingActionButton.OnVisibilityChangedListener

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentPlace,
                fragment
            )
            .commit()
        when (fragment::class) {
            CitySelect::class -> {//changeCityList

                currentFragment = 1
                fab.hide(addVisibilityChanged)
                invalidateOptionsMenu()
                bottomAppBar.navigationIcon = null
                bottomAppBar.hideOnScroll = true
            }
            CityWeather::class -> {//weather in city

                currentFragment = 0
                fab.hide(addVisibilityChanged)
                invalidateOptionsMenu()
                bottomAppBar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu_24)
                bottomAppBar.hideOnScroll = false
            }
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveKey(PREFS_KEY_THEME, prefsMode)
    }

    private fun initTheme() {
        val menuItem = bottomAppBar.menu.findItem(R.id.mi_theme)
        when (loadKey(PREFS_KEY_THEME)) {
            THEME_LIGHT -> {
                menuItem.setIcon(R.drawable.ic_night_24)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                isDark = false
            }
            THEME_DARK -> {
                menuItem.setIcon(R.drawable.ic_day_24)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                isDark = true
            }
            else ->
                if (menuItem.itemId == R.id.mi_theme)
                    menuItem.setIcon(R.drawable.ic_night_24)
        }
    }

    private fun showSnackMessage(text: CharSequence) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
            .setAnchorView((if (fab.visibility == View.VISIBLE) fab else bottomAppBar as View))
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        //val adapter = SavedCityListAdapter(this)
        viewModel = ViewModelProvider(this).get(GlobalViewModel::class.java).also {
            it.adapter.setLists(
                resources.getStringArray(R.array.citys_array).toList(),
                GlobalViewModel.DEFAULT_ID
            )
//            it.allCitys.observe(this, Observer { citys ->
//                citys?.let{ adapter.setWords(citys) }
//            })
        }

        viewModel.databaseCitys.observe(this, Observer { citys ->
            citys?.let { viewModel.adapter.setDatabaseList(it) }
        })



        // recyclerView.adapter = adapter
        // recyclerView.layoutManager = LinearLayoutManager(this)


        initTheme()
        setSupportActionBar(bottomAppBar)

        addVisibilityChanged = object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                bottomAppBar.toggleFabAlignment()
                bottomAppBar.replaceMenu(
                    if (currentFragment == 1) //if (currentFabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)
                        R.menu.bottomappbar_menu_secondary
                    else R.menu.bottomappbar_menu_primary
                )
                fab?.setImageDrawable(
                    if (currentFragment == 1) //if (currentFabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)
                        ContextCompat.getDrawable(application, R.drawable.ic_check_24)
                    else
                        ContextCompat.getDrawable(application, R.drawable.ic_change_city_24)
                )
                fab?.show()
            }
        }

        fab.setOnClickListener {
            if (currentFragment == 0) {
                CommonWeather.getData(viewModel.adapter.getNext().id, viewModel)
            }
            else {
                viewModel.adapter.applyCheck()
                changeFragment(
                    CityWeather
                        .newInstance(
                            viewModel.adapter.getFirst().id
                        )
                )
            }
        }

        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл (регулируется параметром)

        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл (регулируется параметром)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Получить клиента для регистрации, а также данных по клиенту

        // Получить клиента для регистрации, а также данных по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // кнопка регистрации пользователя

        // кнопка регистрации пользователя
        signinButton.setOnClickListener(View.OnClickListener {
            signIn()
        })

        when (currentFragment) {
            0 -> changeFragment(CityWeather.newInstance(GlobalViewModel.DEFAULT_ID))
            else -> changeFragment(CitySelect.newInstance(0))
        }


    }

    override fun onStart() {
        super.onStart()

        // Проверим, заходил ли пользователь в этом приложении через Гугл
        val account = GoogleSignIn.getLastSignedInAccount(this)
        isSigned = account != null
        enableSign()
    }

    // Получение результатов от окна регистрации пользователя
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты по нему уже готовы.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // Инициация регистрации пользователя
    private fun signIn() {
        Log.d("myLOG", "enableSign: ${bottomAppBar.menu.getItem(1)}")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Выход из учетной записи в приложении
    fun signOut() {

        googleSignInClient.signOut()
            .addOnCompleteListener(this) {
                enableSign()
            }
    }

    private fun enableSign(){
        if (isSigned)
            signinButton.visibility = View.GONE
        else
            signinButton.visibility = View.VISIBLE
    }

    //https://developers.google.com/identity/sign-in/android/backend-auth?authuser=1
    // Получение данных пользователя
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Регистрация прошла успешно
            enableSign()
            isSigned = true
        } catch (e: ApiException) {
            isSigned = false
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("myLOG", "signInResult:failed code=" + e.statusCode)
        }
    }


    private fun regReceivers(){
        // Программная регистрация ресивера
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_LOW))
        registerReceiver(connectivityReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

        // инициализация канала нотификаций
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("2", "name", importance)
            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun BottomAppBar.toggleFabAlignment() {
        fabAlignmentMode = if (currentFragment == 0)
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        else BottomAppBar.FAB_ALIGNMENT_MODE_END
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when (currentFragment) {
            0 -> menuInflater.inflate(R.menu.bottomappbar_menu_primary, menu)
            1 -> menuInflater.inflate(R.menu.bottomappbar_menu_secondary, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_theme -> {//Меняем тему
                if (isDark)
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
                else
                    setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
            }
            android.R.id.home -> {//Показываем меню
                val bottomNavDrawerFragment = BottomNavigationDrawerFragment(this)
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
            R.id.mi_back -> {//не изменять список городов
                viewModel.adapter.cancelCheck()
                changeFragment(CityWeather())
            }


            else -> showSnackMessage(item.title)
        }
        return true
    }

    fun getSigned(): Boolean = isSigned

    companion object {
        var currentFragment = 0
        var isSigned = false
    }
}