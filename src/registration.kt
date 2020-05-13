/**
 * Реализовать приложение. Вся информация должна храниться в базе данных.
 *
 * Регистрация пользователя в системе. Должны быть заполнены поля:  Имя, Фамилия,  Дата рождения, Телефон, Город, Адрес.
 * Система должна присваивать уникальный ID,  генерируя его.
 * При регистрации пользователя должна производиться проверка на несовпадение ID.
 * При совпадении имени и фамилии регистрация не должна производиться.
 * */

import java.awt.Button
import java.sql.*
import javax.swing.*
import javax.swing.text.MaskFormatter

fun main(args: Array<String>) {
    createFrame()
}

class GUIApp(title: String) : JFrame() {
    init {
        createUI(title)
    }
    private fun createUI(title: String) {
        setTitle(title)
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(300, 230)
        setLocation(450, 150)
    }
}
fun createButton(str: String) : Button {
    val button = Button()
    button.label = str
    return button
}
fun createFrame() {

    val frame = GUIApp("Регистрация")
    val panel = JPanel()

    val textFieldName = JTextField("", 21)
    panel.add(JLabel("Имя: "))
    panel.add(textFieldName)

    val textFieldSurname = JTextField("", 18)
    panel.add(JLabel("Фамилия: "))
    panel.add(textFieldSurname)

    val dateFormatter = MaskFormatter("####-##-##")
    dateFormatter.placeholderCharacter = '1'
    val textFieldDateOfBirth = JFormattedTextField(dateFormatter)
    textFieldDateOfBirth.columns = 15
    panel.add(JLabel("Дата рождения: "))
    panel.add(textFieldDateOfBirth)

    val textFieldPhone = JTextField("", 18)
    panel.add(JLabel("Телефон: "))
    panel.add(textFieldPhone)

    val textFieldCity = JTextField("", 20)
    panel.add(JLabel("Город: "))
    panel.add(textFieldCity)

    val textFieldAddress = JTextField("", 20)
    panel.add(JLabel("Адрес: "))
    panel.add(textFieldAddress)

    val registrationButton = createButton("Зарегистрироваться")

    registrationButton.addActionListener {

        val userName: String = textFieldName.text
        val userSurname: String = textFieldSurname.text
        val userDateOfBirth: String = textFieldDateOfBirth.text
        val userPhone: String = textFieldPhone.text
        val userCity: String = textFieldCity.text
        val userAddress: String = textFieldAddress.text

        if (userName != ""
            && userSurname != ""
            && userDateOfBirth != ""
            && userPhone != ""
            && userCity != ""
            && userAddress != "") {

            var idUserWithNameSurname: Int = queryIdUser(userName, userSurname)
            if (idUserWithNameSurname == 0) {
                // new user - successful
                var insert: Boolean = queryInsertUser(userName, userSurname, userDateOfBirth, userPhone, userCity, userAddress)
                if (insert) {
                    var idNewUser: Int = queryIdUser(userName, userSurname)
                    JOptionPane.showMessageDialog(panel, "Успешная регистрация:\n" +
                            "ID_$idNewUser $userName $userSurname")
                }
            } else {
                // such user already exists
                JOptionPane.showMessageDialog(panel, "Пользователь с такими именем\n" +
                        "и фамилией уже существует:\n" +
                        "ID_$idUserWithNameSurname $userName $userSurname")
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Заполните все поля")
        }
    }
    panel.add(registrationButton)
    frame.add(panel)
    frame.isVisible = true
    frame.isResizable = false
}

fun queryIdUser(nameText: String, surnameText: String): Int {

    var connection: Connection? = connection()
    var sqlNameSurnameUser = "select * from registration where name = ? and surname = ?"
    var preparedStatement: PreparedStatement
    var idResultSet: ResultSet? = null
    if (connection != null) {
        preparedStatement = connection.prepareStatement(sqlNameSurnameUser)
        preparedStatement.setString(1, nameText)
        preparedStatement.setString(2, surnameText)
        idResultSet = preparedStatement.executeQuery()
    }
    var idInt = 0
    if (idResultSet != null) {
        while(idResultSet.next()) {
            idInt = idResultSet.getInt("id")
        }
    }
    return idInt
}

fun queryInsertUser(nameText: String, surnameText: String, dateOfBirthText: String,
                    phoneText: String, cityText: String, addressText: String): Boolean {

    var connection: Connection? = connection()
    var sqlInsertUser =
        "insert into registration (name, surname, dateOfBirth, phone, city, address) values (?, ?, ?, ?, ?, ?)"
    var preparedStatement: PreparedStatement
    return if (connection != null) {
        preparedStatement = connection.prepareStatement(sqlInsertUser)
        preparedStatement.setString(1, nameText)
        preparedStatement.setString(2, surnameText)
        preparedStatement.setString(3, dateOfBirthText)
        preparedStatement.setString(4, phoneText)
        preparedStatement.setString(5, cityText)
        preparedStatement.setString(6, addressText)
        preparedStatement.executeUpdate()
        true
    } else {
        false
    }
}

fun connection(): Connection? {
    val jdbcDriver = "com.mysql.jdbc.Driver"
    val url: String = "jdbc:mysql://localhost:3306/registrationdb" + "?useSSL=false"
    val user = "root"
    val password = "root"

    var connection: Connection? = null
    try {
        Class.forName(jdbcDriver)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    try {
        connection = DriverManager.getConnection(url, user, password)
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    return connection
}

//*
// create database registrationdb
//
// create table registration (
//    id MEDIUMINT not null auto_increment,
//    name char(30),
//    surname char(30),
//    dateOfBirth date,
//    phone int(15),
//    city char(30),
//    address char(30)
//)
// */