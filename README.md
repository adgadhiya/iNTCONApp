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

Navigation Bar contains two different views. 


1. One view contains list of all the groups which the  user part of.
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-29-223306.png" width="275" height="500"/>
</p>
2. Other view contains Option to create groups, Join Groups, Sent Request, Group Members, Group Details and Other Stuff.
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/Screenshot_2017-04-04-23-40-44-218.png" width="275" height="500"/>
</p>

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

This two class contains code for the <strong>SECOND TAB</strong>. <br> Campaign_Fragment.java is fragment with required methods like load Events, load old achievements and when user refresh the tab look for new post(Events).<br>

<strong>Adapter class for this fragment is adapted from Achievement(AchivementListAdapter.java)</strong>

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-092104.png" width="275" height="500"/>
</p>

<h3>Campaign_Detail.java, CampaignDetailProvider.java</h3>

Campaign_Detail.java is an activity which will lainch after clicking an item from event list.

CampaignDetailProvider.java contains all the required information of the event like Event title, Event Detail, Event Date, Registration Fees, prize, venue, Contact Detail, Last date for registration, Event Date, Accommodation etc.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-091930.png" width="275" height="500"/>
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-11-19-091952.png" width="275" height="500"/>
</p>


<h3>AddCampaign.java<h3>
All the achievement listed in Achievement_Fragment are added from this activity. 

<h3>Campaign_Chat.java</h3>
People can chat with event organizers and other people from this activity. This activity will open when user clicks the BLUE COLOR button with number.

<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2017-03-29-231032.png" width="275" height="500"/>
</p>

<h3>KeysAndGroupKeys.java, URLSpanNoUnderline.java</h3>
This two classes are to support Campaign_Detail.java class(Activity).


<body><p style="color:#FF0000";>Red paragraph text</p></body>

<h1>3. DataResource</h1>
DataResource_Fragment.java
DataResourceAdapter.java
DataResourceProvider.java

ChooseResource.java
DataResourceItemProvider.java
DataResourceItems.java
UploadResourceAdapter.java

<h1>4. Downloads</h1>
DownloadResourcesActivity.java
DownloadService.java
DownloadedResourcesAdapter.java
DownloadingResourcesAdapter.java

<h1>5. InstRequest</h1>

CoursesOffered.java
FieldDetail.java


<h1>6. NavigationView</h1>
CreateGroup.java
FAQ.java
FAQClass.java
GroupInfo.java
GroupList.java
GroupMemberViewAdapter.java
GroupMembers.java
GroupRequest.java
IntconGroupAdapter.java
JoinGroup.java
PrivacyPolicy.java
SearchGroupAdapter.java
SentRequest.java
UserGroupListAdapter.java
UserGroupListClass.java
UserRequest.java
UserRequestDetailClass.java


<h1>7. Notice</h1>
AddNotice.java
My_Notice.java
NoticeChatListAdapter.java
NoticeChatListProvider.java
NoticeListAdapter.java
NoticeListProvider.java
Notice_Fragment.java
Notices_Chat.java


<h1>8. OtherClasses</h1>
AvatarImageBehavior.java
BoolClass.java
BugReport.java
BugReportClass.java
CheckableRelativeLayout.java
CustomList.java
CustomListSingleOnly.java
DashBoard.java
DialogHandler.java
FileSelectionActivity.java
FragmentAdapter.java
GlobalNames.java
GroupProfile.java
ImageSaver.java
Like_Interested_Going_Bool.java
Like_Interested_Going_Cnt.java
MyApplication.java
NetworkConnectionCheck.java
OurClient.java
SquareImageView.java
ThreeHFourthWImageView.java
UserProfile.java


<h1>9. Preferences</h1>
AllPreferences.java


<h1>10. SQLiteDataBase</h1>
SQLiteDataBaseCheckChange.java
SQLiteDataBaseHelperClass.java
SqliteDataBaseDownloadedOrError.java
SqliteDataBaseDownloading.java
SqliteDataBaseShare.java
SqliteDataBaseUploadedOrError.java
SqliteDataBaseUploading.java


<h1>11. SignIn</h1>
ForgotPassword.java
MainActivity.java
Profile.java
SignUpHere.java


<h1>12. Uploads</h1>
UploadResourcesListActivity.java
UploadService.java
UploadedResourceListAdapter.java
UploadingResourcesAdapter.java


<h1>13. ViewHolders</h1>
DataResourceItemHolder.java
FAQViewHolder.java
FieldHolder.java
UserRequestViewHolder.java
