xml.instruct! :xml, :version =>"1.0"
xml.rss :version => "2.0" do
    xml.channel do
    xml.title "News"
    xml.description "All news from ZONE"
    #xml.link formatted_posts_url(:rss)
    
    for element in @result
      next if (element[1].select { |f| f[0] == "http://purl.org/rss/1.0/title"})[0] == nil
      next if (element[1].select { |f| f[0] == "http://purl.org/rss/1.0/pubDate"})[0] == nil
      next if (element[1].select { |f| f[0] == "http://purl.org/rss/1.0/description"})[0] == nil
      
      xml.item do
        xml.title ((element[1].select { |f| f[0] == "http://purl.org/rss/1.0/title"})[0])[1]
        if ((element[1].select { |f| f[0] == "http://purl.org/rss/1.0/description"})[0]) != nil
            xml.description  ((element[1].select { |f| f[0] == "http://purl.org/rss/1.0/description"})[0])[1].html_safe
        else
            xml.description  ""
        end
        xml.pubDate Time.parse(((element[1].select { |f| f[0] == "http://purl.org/rss/1.0/pubDate"})[0])[1]).to_s(:rfc822)#.to_s(:rfc822)
        
        xml.link element[0]
        
        image = (element[1].select { |f| f[0] == "http://purl.org/rss/1.0/image"})[0]
        if image != nil
            xml.enclosure url:image[1], type:"image/jpeg"
        end
     end
    end
  end
end