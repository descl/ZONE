class Filter
  include ActiveModel::Serialization
    
  attr_accessor :prop, :value
  
  def initialize(attributes = {})  
    attributes.each do |name, value|  
      if ! value.start_with? 'http://'
        value = 'http://'+value
      end
      send("#{name}=", value)  
    end  
  end
  def to_param
     {:prop => @prop.sub("http://",""), :value => @value.sub("http://","")}
  end
end
