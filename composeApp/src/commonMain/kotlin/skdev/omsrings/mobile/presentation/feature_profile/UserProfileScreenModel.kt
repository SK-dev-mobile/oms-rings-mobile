package skdev.omsrings.mobile.presentation.feature_profile

import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager

class UserProfileScreenModel(
    notificationManager: NotificationManager
) : BaseScreenModel<UserProfileContract.Event, UserProfileContract.Effect>(notificationManager) {


    override fun onEvent(event: UserProfileContract.Event) {
        TODO("Not yet implemented")
    }
    

}