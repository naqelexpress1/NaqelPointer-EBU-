package com.naqelexpress.naqelpointer.Activity.InterCity;
//
public class Todelete {

//    {"tripId":"12345","vehicleTractorHeadAndTrailer":"Noshad","driverEmpId":"24316","driverEmpName":"Noshad","driverContactNo":"Noshad","damageOrPuncture":"1","greaseHubCup":"1","spareTire":"1","singleDeckerSideBarAvailable":"1","doubleDeckerSideBarAvailable":"1","checkCurtainLockingRatchet":"1","checkCurtainsBeltAndTears":"1","cargoBeltApplied":"1","checkCurtainsRollers":"1","checkCurtainsCleanliness":"1","curtainBeltLock":"1","checkRearDoorBolts":"1",
//            "checkRearDoorLocks":"1","checkSlidingSupportPostAndItsLocking":"1","checkNumberPlateAndHolder":"1","checkAirLeak":"1","checkAirSuspensionCondition":"1","trailerBodyRemarks":"Noshad","landingLegFunctional":"1","landingLegShoes":"1",
//            "checkLightConditions":"1","fireExtinguisherAvailability":"1","fireExtinguisherValidity":"1","TireConditionOne":"ImageOne.jpg","TireConditionTwo":"ImageTwo.jpg","TireConditionThree":"ImageThree.jpg",
//            "TireConditionFour":"ImageFour.jpg","SafetyCurtainsAndCargoOne":"ImageOne.jpg","SafetyCurtainsAndCargoTwo":"ImageTwo.jpg","SafetyCurtainsAndCargoThree":"ImageThree.jpg","SafetyCurtainsAndCargoFour":"ImageFour.jpg","OtherAttachmentOne":"ImageOne.jpg",
//            "OtherAttachmentTwo":"ImageTwo.jpg","OtherAttachmentThree":"ImageThree.jpg","OtherAttachmentFour":"ImageFour.jpg","OtherAttachmentFive":"ImageOne.jpg","OtherAttachmentSix":"ImageTwo.jpg","OtherAttachmentSeven":"ImageThree.jpg","OtherAttachmentEight":"ImageFour.jpg"}

//    {"tripId":"12345","vehicleTractorHeadAndTrailer":"Noshad","driverEmpId":"24316","driverEmpName":"Noshad",
//            "driverContactNo":"Noshad","damageOrPuncture":"1","greaseHubCup":"1","spareTire":"1","singleDeckerSideBarAvailable":"1",
//            "doubleDeckerSideBarAvailable":"1","checkCurtainLockingRatchet":"1","checkCurtainsBeltAndTears":"1","cargoBeltApplied":"1",
//            "checkCurtainsRollers":"1","checkCurtainsCleanliness":"1","curtainBeltLock":"1","checkRearDoorBolts":"1",
//            "checkRearDoorLocks":"1","checkSlidingSupportPostAndItsLocking":"1","checkNumberPlateAndHolder":"1","checkAirLeak":"1",
//            "checkAirSuspensionCondition":"1","trailerBodyRemarks":"Noshad","landingLegFunctional":"1","landingLegShoes":"1",
//            "checkLightConditions":"1","fireExtinguisherAvailability":"1","fireExtinguisherValidity":"1",
//            "TireConditionOne":"ImageOne.jpg","TireConditionTwo":"ImageTwo.jpg","TireConditionThree":"ImageThree.jpg",
//            "TireConditionFour":"ImageFour.jpg","SafetyCurtainsAndCargoOne":"ImageOne.jpg","SafetyCurtainsAndCargoTwo":"ImageTwo.jpg",
//            "SafetyCurtainsAndCargoThree":"ImageThree.jpg","SafetyCurtainsAndCargoFour":"ImageFour.jpg","OtherAttachmentOne":"ImageOne.jpg",
//            "OtherAttachmentTwo":"ImageTwo.jpg","OtherAttachmentThree":"ImageThree.jpg","OtherAttachmentFour":"ImageFour.jpg",
//            "OtherAttachmentFive":"ImageOne.jpg","OtherAttachmentSix":"ImageTwo.jpg","OtherAttachmentSeven":"ImageThree.jpg",
//            "OtherAttachmentEight":"ImageFour.jpg"}



//// POST: api/FileUploads
//    [ResponseType(typeof(FileUpload))]
//    public IHttpActionResult PostFileUpload()
//    {
//        if (HttpContext.Current.Request.Files.AllKeys.Any())
//        {
//            // Get the uploaded image from the Files collection
//            var httpPostedFile = HttpContext.Current.Request.Files["UploadedImage"];
//            if (httpPostedFile != null)
//            {
//                FileUpload imgupload = new FileUpload();
//                int length = httpPostedFile.ContentLength;
//                imgupload.imagedata = new byte[length]; //get imagedata
//                httpPostedFile.InputStream.Read(imgupload.imagedata, 0, length);
//                imgupload.imagename = Path.GetFileName(httpPostedFile.FileName);
//                db.FileUploads.Add(imgupload);
//                db.SaveChanges();
//                // Make sure you provide Write permissions to destination folder
//                string sPath = @"C:\Users\xxxx\Documents\UploadedFiles";
//                var fileSavePath = Path.Combine(sPath, httpPostedFile.FileName);
//                // Save the uploaded file to "UploadedFiles" folder
//                httpPostedFile.SaveAs(fileSavePath);
//                return Ok("Image Uploaded");
//            }
//        }
//        return Ok("Image is not Uploaded");
//    }
//
//        {"tripId":"123",
//        "vehicleTractorHeadAndTrailer":"ad adsfk",
//        "driverEmpId":"12312",
//        "driverEmpName":"nosa",
//        "driverContactNo":"21546546",
//        "damageOrPuncture":"1",
//        "greaseHubCup":"1",
//        "spareTire":"1",
//        "singleDeckerSideBarAvailable":"1",
//        "doubleDeckerSideBarAvailable":"1",
//        "checkCurtainLockingRatchet":"1",
//        "checkCurtainsBeltAndTears":"1",
//        "cargoBeltApplied":"1",
//        "checkCurtainsRollers":"1",
//        "checkCurtainsCleanliness":"1",
//        "curtainBeltLock":"1",
//        "checkRearDoorBolts":"1",
//        "checkRearDoorLocks":"1",
//        "checkSlidingSupportPostAndItsLocking":"1",
//        "checkNumberPlateAndHolder":"1",
//        "checkAirLeak":"1",
//        "checkAirSuspensionCondition":"1",
//        "trailerBodyRemarks":"no remarks",
//        "landingLegFunctional":"1",
//        "landingLegShoes":"1",
//        "checkLightConditions":"1",
//        "fireExtinguisherAvailability":"1",
//        "fireExtinguisherValidity":"1"}
//
//
//
////            tripId:123
////            vehicleTractorHeadAndTrailer:ad adsfk
////            driverEmpId:12312
////            driverEmpName:nosa
////            driverContactNo:21546546
////            damageOrPuncture:1
////            greaseHubCup:1
////            spareTire:1
////            singleDeckerSideBarAvailable:1
////            doubleDeckerSideBarAvailable:1
////            checkCurtainLockingRatchet:1
////            checkCurtainsBeltAndTears:1
////            cargoBeltApplied:1
////            checkCurtainsRollers:1
////            checkCurtainsCleanliness:1
////            curtainBeltLock:1
////            checkRearDoorBolts:1
////            checkRearDoorLocks:1
////            checkSlidingSupportPostAndItsLocking:1
////            checkNumberPlateAndHolder:1
////            checkAirLeak:1
////            checkAirSuspensionCondition:1
////            trailerBodyRemarks:no remarks
////            landingLegFunctional:1
////            landingLegShoes:1
////            checkLightConditions:1
////            fireExtinguisherAvailability:1
////            fireExtinguisherValidity:1
}
