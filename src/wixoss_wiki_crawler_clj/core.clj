(ns wixoss-wiki-crawler-clj.core
  (:use [net.cgrand.enlive-html])
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn get-cardlist []
  (let [get-card-detail-uris
        (fn [id page]
          (let [cardlist-uri (str "http://www.takaratomy.co.jp/products/wixoss/card/card_list.php?product_id=" id "&page=" page )
                cardlist-html (with-open [rdr (io/reader cardlist-uri)] (html-resource rdr))
                card-detail-uris 
                (rest (map #(:href (:attrs %1))
                           (select (first (select cardlist-html [:div (attr= :id "column2_box_main_inner") :table])) [:a])))]
            (cond (and (= (count card-detail-uris) 0) (= id 1)) nil
                  (= (count card-detail-uris) 0) (concat card-detail-uris (get-card-detail-uris (+ id 1) 1))
                  :else (concat card-detail-uris (get-card-detail-uris id (+ page 1))))))
        card-detail-uris (map #(str "http://www.takaratomy.co.jp/products/wixoss/card/card_detail.php?id=" (string/replace %1 #"[^0-9]" "")) (get-card-detail-uris 0 1))
        get-card-detail 
        (fn [card-detail-uri]
          (let [card-detail-html (with-open [rdr (io/reader card-detail-uri)] (html-resource rdr))
                card-detail-title (select card-detail-html [:div (attr= :class "card_detail_title")])
                get-content-in-card-detail-title (fn [tagname] (first (apply :content (select card-detail-title [(keyword tagname)]))))
                card-detail-data (map #(first (:content %1)) (select card-detail-html [:table :tr :td]))]
            (assoc {}
              :id (get-content-in-card-detail-title "p")
              :name (get-content-in-card-detail-title "h3")
              :pronunciation (re-find #"[^＜][^＞]*" (get-content-in-card-detail-title "span"))
              :type (nth card-detail-data 0)
              :class (nth card-detail-data 1)
              :color (nth card-detail-data 2)
              :level (nth card-detail-data 3)
              :growcost (nth card-detail-data 4)
              :cost (nth card-detail-data 5)
              :limit (nth card-detail-data 6)
              :power (nth card-detail-data 7)
              :limitcondition (nth card-detail-data 8)
              :gurd (nth card-detail-data 9)
              :effect (last (:content (nth (select card-detail-html [:table :tr :td]) 10))))))]
    (take 10 card-detail-uris)))

(get-cardlist)
(take 10 )

(first (get-cardlist))
(string/replace "./card_detail.php?id=149" )
(clojure.pprint/pprint
 (get-cardlist)
 )

(and true true)


(comment
  {
   :id "WX01-001"
   :name "満月の巫女　ヤマヨリヒメ"
   :pronunciation "ﾏﾝｹﾞﾂﾉﾐｺﾀﾏﾖﾘﾋﾒ"
   :type "ルリグ"
   :class "タマ"
   :color "白"
   :level "4"
   :growcost "白x3"
   :cost "-"
   :limit "11"
   :power "-"
   :limitcondition "-"
   :gurd "-"
   :effect {:常時能力 "あなたの場に<<甲冑　ローメイル>>があるかぎり、あなたのすべてのシグにのパワーを+2000"}
   :recording "WD01"}
  )

