private fun setupCategories(menu: View) {
    val title = menu.findViewById<TextView>(R.id.menuTitle)
    val funcName1 = menu.findViewById<TextView>(R.id.funcName1)
    
    val catControl = menu.findViewById<TextView>(R.id.catControl)
    val catAssistant = menu.findViewById<TextView>(R.id.catAssistant)
    val catFunctions = menu.findViewById<TextView>(R.id.catFunctions)
    val catAutomation = menu.findViewById<TextView>(R.id.catAutomation)
    val catSettings = menu.findViewById<TextView>(R.id.catSettings)
    val catInfo = menu.findViewById<TextView>(R.id.catInfo)

    val allCats = listOf(catControl, catAssistant, catFunctions, catAutomation, catSettings, catInfo)

    fun selectCategory(selectedCat: TextView, catTitle: String, functionText: String) {
        for (cat in allCats) {
            cat.setBackgroundColor(Color.parseColor("#1A2B3C"))
            cat.setTextColor(Color.WHITE) // Текст всегда белый
        }
        // Подсвечиваем выбранную (фон голубой, но текст все равно белый)
        selectedCat.setBackgroundColor(Color.parseColor("#8A9CF5"))
        selectedCat.setTextColor(Color.WHITE)
        
        title.text = catTitle
        funcName1.text = functionText
    }

    // Устанавливаем изначально выбранную категорию
    selectCategory(catFunctions, "Functions", "")

    catControl.setOnClickListener { selectCategory(catControl, "Control", "Mouse") }
    catAssistant.setOnClickListener { selectCategory(catAssistant, "Assistant", "") }
    catFunctions.setOnClickListener { selectCategory(catFunctions, "Functions", "") }
    catAutomation.setOnClickListener { selectCategory(catAutomation, "Automation", "") }
    catSettings.setOnClickListener { selectCategory(catSettings, "Settings", "") }
    catInfo.setOnClickListener { selectCategory(catInfo, "information", "") }
}
