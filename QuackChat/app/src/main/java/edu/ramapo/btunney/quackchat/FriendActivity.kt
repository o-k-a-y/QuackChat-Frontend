package edu.ramapo.btunney.quackchat

//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.ramapo.btunney.quackchat.networking.NetworkCallback
import edu.ramapo.btunney.quackchat.networking.NetworkRequester
import edu.ramapo.btunney.quackchat.networking.ServerRoutes

class FriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        // Load all of user's friend

        // 1. Check if hash in local DB is correct
        // Get hash from Room DB
        NetworkRequester.validateHash(ServerRoutes.CHECK_HASH, "insert hash", object: NetworkCallback {
            override fun onFailure(failureCode: NetworkCallback.FailureCode) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(data: Any?) {
                TODO("Not yet implemented")
            }

        })
        // 2. Update friend list of not updated
        // 3. Once friend list is up to date:
        //      create a new view to add to horizontal scroll view containing:
        //          a. name of friend (start with just this one)
        //          b. image of friend?
    }
}
