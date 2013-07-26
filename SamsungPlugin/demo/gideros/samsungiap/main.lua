local str = TextField.new(nil,"ooo")
str:setPosition(100,100)
stage:addChild(str)

if application:getDeviceInfo() == "Android" then
	require("samsungiap")

	local itemGroupId = "100000100010"
	local itemId = "000001000018"
	samsungiap:purchaseItem(itemGroupId,itemId)
	
	onPurchaseStateChange = function(event)
		if (event.purchaseState == SamsungIAP.CANCELED) then
			-- show Message , it's canceled
			print("puchase canceled")
		elseif (event.purchaseState == SamsungIAP.PURCHASED) then
			AlertDialog.new("Congras","Item Purchased!",""):show()
			-- unlock here
		end
	end
	
	samsungiap:addEventListener(Event.PURCHASE_STATE_CHANGE, onPurchaseStateChange)
end