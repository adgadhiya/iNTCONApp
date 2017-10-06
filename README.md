# iNTCONApp


This app is available on google play store. After learning android, I created this app.

iNTCON 
iNTCON is an App, using you can create groups or join groups and you can share many things which are listed below. or You can join our pre-created groups and download materials. Right now we've provided GTU Exam papers for all branches that you can download with single click. Below all the Apps features are given.

iNTCON Resources:
Sharing materials with your friends is easy task but sharing same material again and again makes you frustrated. Well you create your own group and Share your upload your material only one time. You can share same material across groups without uploading that second time.

iNTCON NOTICES:
Share notices with your friends and students anytime with attachment. So, forget Notice Board when you have Notice board in your Pocket.


iNTCON ACHIEVEMENTS:
Achievements plays a big role in institutes fame. But what if no one knows about institutes Achievements.By using this feature we can share our achievements with other Peoples.


iNTCON EVENTS:
Institute always organizes Events and Competitions. Because of lake of time it is not possible for us to send students for campaign every time. By using iNTCON Events we can tell others about the competition or Events or Workshops etc. 

It contains all the information related to Events like Events description. Event date and time, Participation fees, Last date for registration, Accommodation availability, Winner prize etc.

--------------------------------------------------------------------------------------------------------------------------------------

<h3>Firebase database is used as backend server provided by google.If you want to know more about firebase you can read from :: https://firebase.google.com.</h3>

<h3>NOTE:: FACE_BOOK LOGIN and LIKE will not work, because i've deactivated that feature.</h3>

There are total 4 tabs in the app.
1. Achievement
2. Notice
3. Resources
4. Events

It contains total 13 different packages.

<h1> 1. Achievement </h1>

<h3> AchievementListProvider.java, 
Achievement_Fragment.java, 
AchivementListAdapter.java </h3>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-12-17-103513.png" width="275" height="500"/>
</p>


This three class contains code for the <strong>FIRST TAB</strong>. <br> 
Achievement_Fragment.java is fragment with required methods like load achievements, load old achievements and when user refresh the tab look for new post(Achievement). <br>

<strong>Please also check it's XML file(front end Design)</strong>

AchievementListProvider.java contains a class with all required fields like group_key, title, date, imagepath, key, content, user id, likes, group_type etc.<br>

AchivementListAdapter.java is an Adapter class implemented from Recyclerview. This class also handles click events.<br>

<h3>AchievementDetail.java</h3>
This is new activity. This activity will be launched when uesr click item from Achievement_Fragment class. This class provides the information about the Achievement.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-12-17-103850.png" width="275" height="500"/>
</p>

<strong>Please also check it's XML file(front end Design)</strong><br>

<h3>AddAchievement.java</h3>
All the achievement listed in Achievement_Fragment are added from this activity. 

<h1>2. Campaign</h1>

<h3>Campaign_Fragment.java, CampaignListAdapter.java</h3>

This two class contains code for the <strong>LAST TAB</strong>. <br> Campaign_Fragment.java is fragment with required methods like load Events, load old Evenets and when user refresh the tab look for new post(Events).<br>

<strong>Adapter class for this fragment is adapted from Achievement(AchivementListAdapter.java)</strong>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-092104.png" width="275" height="500"/>
</p>

<h3>Campaign_Detail.java, CampaignDetailProvider.java</h3>

Campaign_Detail.java is an activity which will lainch after clicking an item from Campaign_Fragment.

CampaignDetailProvider.java contains all the required information of the event like Event title, Event Detail, Event Date, Registration Fees, prize, venue, Contact Detail, Last date for registration, Event Date, Accommodation etc.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-091930.png" width="275" height="500"/>
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-091952.png" width="275" height="500"/>
</p>


<h3>AddCampaign.java</h3>
All the Events listed in Campaign_Fragment are added from this activity. 

<h3>Campaign_Chat.java</h3>
People can chat with event organizers and other people from this activity. This activity will open when user clicks the <strong>BLUE COLOR</strong> button with number.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-29-231032.png" width="275" height="500"/>
</p>

<h3>KeysAndGroupKeys.java, URLSpanNoUnderline.java</h3>
This two classes are to support Campaign_Detail.java class(Activity).

<h2>Share on Facebook and Google+</h2>
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-093556.png" width="275" height="500"/>
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-093707.png" width="275" height="500"/>
</p>


<h1>3. DataResource</h1>

<h3>DataResource_Fragment.java, DataResourceAdapter.java, DataResourceProvider.java</h3>

This three class contains code for the <strong>THIRD TAB</strong>. <br> DataResource_Fragment.java is fragment with required methods like load list of uploaded materials for given grou and when user refresh the tab look for recently uploaded Materials.<br>
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-29-220919.png" width="275" height="500"/>
</p>


<h3>DataResourceItems.java, DataResourceItemProvider.java</h3>

DataResourceItems.java is an activity which will lainch after clicking an item from Item from DataResource_Fragment.<br>
<h4>I've used FirebaseRecyclerAdapter instead of using my custom adapter to show list</h4>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-29-225508.png" width="275" height="500"/>
</p>

Options with FAB will be available only to the user who have created this thread. <br>
Add button is used to add any knid of material upto 100MB.<br>
Share Button is used to share materials with other thread or with other group without uploading all the mateials second time.<br>
Edit button is used to edit name of the material.<br>
Delete button is used to delete any matetial from the list<br>

<h3>ChooseResource.java
UploadResourceAdapter.java</h3>
This two classes are to support user while uploading and choosing the materials to thread.


<h1>4. Downloads</h1>

<h3>DownloadResourcesActivity.java, DownloadService.java, DownloadedResourcesAdapter.java, DownloadingResourcesAdapter.java</h3>

This package provides backend facility when user download the materials.

<strong>DownloadResourcesActivity.java</strong> provides the list of the the materials being downloaded and already downloaded.<br>

<strong>DownloadService.java</strong> runs in back ground. When download completes this service will be destroyed.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-104056.png" width="275" height="500"/>
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-104145.png" width="275" height="500"/>
</p>

<h1>5. InstRequest</h1>

<h3>CoursesOffered.java
FieldDetail.java</h3>

This is simple package which provides the cources offered by the institutes.

<h1>6. NavigationView</h1>

1. One view contains list of all the groups which the  user part of.
2. Other view contains Option to create groups, Join Groups, Sent Request, Group Members, Group Details and Other Stuff.
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-29-223306.png" width="275" height="500"/>
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/Screenshot_2017-04-04-23-40-44-218.png" width="275" height="500"/>
</p>


<h3>UserRequest.java, UserRequestDetailClass.java</h3>

UserRequests.java activity provides the list of all the users who wished to part of given group.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-26-172707.png" width="275" height="500"/>
</p>


<h3>GroupMembers.java, GroupMemberViewAdapter.java</h3>

This Activity provides the list of all the users, who are part of the group.

<h3>GroupInfo.java, GroupList.java</h3>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-115609.png" width="275" height="500"/>
</p>

<h3>CreateGroup.java, GroupRequest.java</h3>
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-22-125703.png" width="275" height="500"/>
</p>

<h3>JoinGroup.java, IntconGroupAdapter.java, SearchGroupAdapter.java, UserGroupListAdapter.java, UserGroupListClass.java</h3>
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-120154.png" width="275" height="500"/>
</p>


<h3>SentRequest.java</h3>
This activity contains all the requests sent by user to groups.

<h3>PrivacyPolicy.java<h3>

<h3>FAQ.java, FAQClass.java</h3>
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-120643.png" width="275" height="500"/>
</p>


<h1>7. Notice</h1>

<h3>Notice_Fragment.java, My_Notice.java, NoticeListProvider.java, NoticeListAdapter.java</h3>
This classes contains code for the <strong>SECOND TAB</strong>. <br> Notice_Fragment.java is fragment with required methods like load Notices, load old Notices and when user refresh the tab look for new post(Notice).<br>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-12-12-110228.png" width="275" height="500"/>
</p>

<h3>Notices_Chat.java, NoticeChatListAdapter.java, NoticeChatListProvider.java</h3>
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-12-17-100414.png" width="275" height="500"/>
</p>

<h3>AddNotice.java</h3>

<h1>8. OtherClasses</h1>
<h3>DashBoard.java, FragmentAdapter.java</h3>
This is the main activity with inflates all the four fragments and it will launch first after user logged in.<br>
FragmentAdapter.java is adpter for all the fragments.<br>

<h3>GlobalNames.java</h3>
All the global variables are initialized in this class.<br>


<h3>AvatarImageBehavior.java<br>
BoolClass.java<br>
BugReport.java<br>
BugReportClass.java<br>
CheckableRelativeLayout.java<br>
CustomList.java<br>
CustomListSingleOnly.java<br>
DialogHandler.java<br>
FileSelectionActivity.java<br>
GroupProfile.java<br>
ImageSaver.java<br>
Like_Interested_Going_Bool.java<br>
Like_Interested_Going_Cnt.java<br>
MyApplication.java<br>
NetworkConnectionCheck.java<br>
OurClient.java<br>
SquareImageView.java<br>
ThreeHFourthWImageView.java<br>
UserProfile.java<br></h3>

All the other class to support the app.<br>

<h1>9. Preferences</h1>
<h3>AllPreferences.java</h3>
All the preference variables are defined in this class.


<h1>10. SQLiteDataBase</h1>
<h3>SqliteDataBaseDownloadedOrError.java, SqliteDataBaseDownloading.java</h3>
Local database to store how many materials downloaded at given time and wheather the material is downloaded or some error occurred.

<h3>SqliteDataBaseUploadedOrError.java, SqliteDataBaseUploading.java</h3>
Local database to store how many materials uploaded at given time and wheather the material is uploaded successfully or some error occurred.

<h3>SQLiteDataBaseCheckChange.java, SQLiteDataBaseHelperClass.java, SqliteDataBaseShare.java</h3>
Other classed to support the app.


<h1>11. SignIn</h1>
<h3>ForgotPassword.java, MainActivity.java, Profile.java, SignUpHere.java</h3>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-095745.png" width="275" height="500"/>
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-10-06-095818.png" width="275" height="500"/>
</p>

<h1>12. Uploads</h1>
<h3>UploadResourcesListActivity.java, UploadService.java, UploadedResourceListAdapter.java, UploadingResourcesAdapter.java</h3>
This package provides backend facility when user download the materials.<br>
<strong>UploadResourcesListActivity.java</strong> provides the list of the the materials being uploaded and already uploaded.<br>
<strong>UploadService.java</strong> runs in back ground. When upload completes this service will be destroyed.

<h1>13. ViewHolders</h1>
<h3>DataResourceItemHolder.java, FAQViewHolder.java, FieldHolder.java, UserRequestViewHolder.java</h3>
