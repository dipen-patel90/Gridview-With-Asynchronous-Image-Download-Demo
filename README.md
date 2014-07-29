Gridview-With-Asynchronous-Image-Download-Demo
==============================================

Gridview demo with asynchronous image download

Android: Gridview demo with download image in background
Adding Demo App for:
* GridView/Custom Adapter
* Image in gridview(Which we will download in background)
* Adding downloaded image in cache/ and adding downloaded image in external storage and will reuse it.




While developing android application we will surely face situation when we have to show image in gridview or in list view.
Here I am adding demo application in which we will show image in gridview, we will download image in background. 
There is two demo code one which will store downloaded image in cache and one will store it in external storage.

Note: We have seen lots of example in which we send image download request from getView() method and we are doing same 
but remember that getView() method calls several time for same value, in that case unnecessarily we will send same 
image download request several times. Thats why we are maintaining list of "image download request" if it is running 
currently we are ignoring same image request and if download fails we are removing image request name from list.
