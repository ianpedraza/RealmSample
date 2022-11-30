package com.ianpedraza.realmsample.data

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val EMPTY_STRING = ""

object Database {
    private val configuration = RealmConfiguration.create(
        schema = setOf(Person::class, Dog::class)
    )

    private val realm = Realm.open(configuration)

    // val configuration2 = RealmConfiguration.Builder(setOf(Person::class, Dog::class))

    fun insert(person: Person) {
        val managerPerson = realm.writeBlocking {
            copyToRealm(person)
        }
    }

    suspend fun insertAsync(person: Person) {
        realm.write {
            copyToRealm(person)
        }
    }

    fun getPeople(): List<Person> {
        return realm.query<Person>().find()
    }

    fun listenPeople(): Flow<List<Person>> {
        return realm.query<Person>().asFlow()
            .map { it.list }
    }

    fun findPerson(name: String): Person? {
        return realm.query<Person>("name = $0", name).first().find()
    }

    suspend fun findPeopleWithDog(dog: Dog): Flow<List<Person>> {
        return realm.query<Person>("dog.age > $0 AND dog.name BEGINSWITH $1", dog.age, dog.name)
            .asFlow()
            .map { it.list }
    }

    suspend fun updateFirstPersonWithoutADog() {
        realm.query<Person>("dog == NULL LIMIT(1)")
            .first()
            .find()
            ?.also { personWithoutADog ->
                realm.write {
                    findLatest(personWithoutADog)?.dog = Dog().apply { name = "Laika" }
                }
            }
    }

    suspend fun deleteAllDogs() {
        realm.write {
            val realmQuery = this.query<Dog>()
            delete(realmQuery)

            // val results: RealmResults<Dog> = query.find()
            // delete(results)

            // results.forEach { delete(it) }
        }
    }
}

class Dog : RealmObject {
    var name: String = EMPTY_STRING
    var age: Int = 0
}

class Person : RealmObject {
    @PrimaryKey
    var name = EMPTY_STRING
    var dog: Dog? = null
}
