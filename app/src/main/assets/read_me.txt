// File text dùng để nắm tiến trình đồ án //
* Những gì đã thêm từ sau buổi họp hôm T2:
- 2 nút đổi tên ở sau 2 cái textview tên người chơi với tên nông trại: bấm vào nút nào đổi tên ở textview
kế bên đó (đã bao gồm điều kiện ràng buộc chỉ được nhập đến tối đa 10 ký tự)
- 1 dialog hướng dẫn người chơi xuất hiện khi nhấn vào hướng dẫn chơi game trong setting
- Xử lý nhấn vào chỗ liên hệ sẽ dẫn đến trang Facebook Farm Valley
- Giao diện đất trồng cho PlantActivity trong đó gồm các imageButton là từng mảnh đất
- Giao diện chuồng trại cho FeedActivity trong đó gồm các imageButton là từng chuồng nuôi
- Nhạc nền cho từng activity cũng như âm thanh cho các control (mở/đóng, lỗi, mua/bán, ...)
- Animation lúa bay cho phần StartActivity (sau 3s sẽ chạy)
* Những gì còn chưa xử lý kịp:
- Xử lý tắt/mở âm thanh và nhạc nền thông qua switch button trong setting (chỉ mới làm xong custom cho
switch button :D)
* Một số lưu ý
- Về cây trồng: có 4 loại cây trồng gồm: lúa mì (wheat), cà rốt (carrot), bắp (corn) và mía (surgarcane).
Mỗi loại cây trồng được chia làm 3 hình thái tương ứng với 3 source ảnh trong drawable:
seed, early_ và ready_
 + seed: dùng chung cho 4 loại cây
 + early_: đây là giai đoạn cây phát triển được 1/2 tổng thời gian, tùy từng loại mà sau đuôi early_... sẽ
là tên loại cây đó
 + ready_: đây là giai đoạn cây đã phát triển toàn diện và sẵn sàng thu hoạch, tùy từng loại mà sau đuôi
ready_... sẽ là tên loại cây đó
- Về vật nuôi: có 4 loại vật nuôi gồm: heo (pig), bò (cow), gà (chicken) và cừu (sheep). Mỗi loại vật nuôi
được chia làm 2 hình thái tương ứng với 2 source ảnh trong drawable:
name, name_feed
 + name (tên trùng với tên động vật): đây là giai đoạn vật nuôi bắt đầu được nuôi nhưng chưa được cho ăn vì vây
sẽ không ra sản phẩm
sau đuôi early_... sẽ là tên vật nuôi đó
 + name_feed: đây là giai đoạn vật nuôi đã được nuôi v sẽ đếm ngược cho ra sản phẩm, tùy từng loại mà
trước đuôi ..._feed sẽ là tên vật nuôi đó
 + Thức ăn của từng loại vật nuôi chính là sản phẩm được thu hoạch từ cây trồng quy đổi ra. Mỗi con cần có 1 bao
thức ăn để bắt đầu nuôi.
 + 1 lúa đổi 1 bao thức ăn cho gà, bò
 + 1 cà rốt đổi 1 bao thức ăn cho heo
 + 1 bắp đổi 1 bao thức ăn cho cừu
- Về sản phẩm, có 8 loại sản phẩm tương ứng với 4 cây trồng và 4 vật nuôi, tùy theo nhu cầu mà người chơi
có thể đem bán hoặc làm thức ăn cho vật nuôi. Trong file drawable, tên mỗi file sản phẩm bắt đầu bằng
tên vật chủ và kết thúc là đuôi _product