xml.instruct! :xml, :version =>"1.0"
xml.rss :version => "2.0" do
    xml.channel do
    xml.title "News"
    xml.description "All news from ZONE"
    #xml.link formatted_posts_url(:rss)
    
    for element in @result
      xml.item do
        xml.title element[1]["http://purl.org/rss/1.0/title"]
        xml.description element[1]["http://purl.org/rss/1.0/description"]
        xml.pubDate Time.parse(element[1]["http://purl.org/rss/1.0/pubDate"]).to_s(:rfc822)#.to_s(:rfc822) Fri, 02 Mar 2012 13:58:03 GMT
                                                                             #Fri Mar 02 11:29:00 CET 2012
        puts Time.parse(element[1]["http://purl.org/rss/1.0/pubDate"]).to_s(:rfc822)
        #xml.link post_url(element["concept"])
        xml.link element[0]
     end
    end
  end
end