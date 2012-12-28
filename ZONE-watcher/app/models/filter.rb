class Filter
  include ActiveModel::Serialization
    
  attr_accessor :prop, :value
  
  def initialize(attributes = {})  
    attributes.each do |name, value|  
      send("#{name}=", value)  
    end  
  end
  def to_param
     {:prop => @prop, :value => @value}
  end
end
