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
<p align="center">
  <img src="https://github.com/adgadhiya/iNTCONApp/blob/master/device-2016-12-17-103513.png" width="275" height="500"/>
</p>



<h3> AchievementListProvider.java, 
Achievement_Fragment.java, 
AchivementListAdapter.java </h3>

This three class contains code for the <strong>FIRST TAB</strong>. <br> 
Achievement_Fragment.java is fragment with required methods like loads achievements, load old achievements and when user refresh the tab look for new post(Achievement). <br>

<strong>Please also check it's XML file(front end Design)</strong>

AchievementListProvider.java contains a class with all required fields like group_key, title, date, imagepath, key, content, user id, likes, group_type etc.<br>

AchivementListAdapter.java is an Adapter class implemented from Recyclerview. This class also handles click events.<br>

<h3>AchievementDetail.java</h3>
This is new activity. This activity will be launched when uesr click item from Achievement_Fragment class. This class provides the information about the Achievement.

<strong>Please also check it's XML file(front end Design)</strong><br>

<h3>AddAchievement.java</h3>
All the achievement listed in Achievement_Fragment are added from this activity. 


<h1>2. Campaign</h1>

<h3>Campaign_Fragment.java, CampaignListAdapter.java, CampaignDetailProvider.java</h3>
<h3>Campaign_Detail.java</h3>
<h3>AddCampaign.java<h3>
<h3>Campaign_Chat.java</h3>
<h3>KeysAndGroupKeys.java
URLSpanNoUnderline.java</h3>

<h1>3. DataResource</h1>
<h1>4. Downloads</h1>
<h1>5. InstRequest</h1>
<h1>6. NavigationView</h1>
<h1>7. Notice</h1>
<h1>8. OtherClasses</h1>
<h1>9. Preferences</h1>
<h1>10. SQLiteDataBase</h1>
<h1>11. SignIn</h1>
<h1>12. Uploads</h1>
<h1>13. ViewHolders</h1>
