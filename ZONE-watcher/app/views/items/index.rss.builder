xml.instruct! :xml, :version =>"1.0"
xml.rss :version => "2.0" do
    xml.channel do
    xml.title "Reador.NET Search: #{@search.getName(t("leftMenu.search"))}"
    xml.description "news comming from Reador.NET"
    #xml.link formatted_posts_url(:rss)
    
    for element in @items
      @item = Item.find(element.uri,current_user)

      next if @item.title == nil
      next if @item.date == nil
      next if @item.description == nil
      xml.item do
        xml.title @item.title
        if @item.description != nil
            xml.description  @item.description[0].html_safe
        else
            xml.description  ""
        end
        xml.pubDate Time.parse(@item.date.to_s(:rfc822))#.to_s(:rfc822)
        
        xml.link @item.uri
        image = @item.props["http://purl.org/rss/1.0/image"]
        if image != nil
            xml.enclosure url:image[0], type:"image/jpeg"
        end
        @item.filters.each do |tag|
          xml.enclosure tag.uri

        end
     end
    end
  end
end