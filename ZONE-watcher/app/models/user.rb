class User < ActiveRecord::Base
  has_many :authentications
  # Include default devise modules. Others available are:
  # :token_authenticatable, :confirmable,
  # :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable, :omniauthable, :mailchimp

  # Setup accessible (or protected) attributes for your model
  attr_accessible :email, :password, :password_confirmation, :remember_me, :provider, :uid, :login, :token, :tokenSecret, :join_mailing_list
  # attr_accessible :title, :body

  def self.find_for_provider_oauth(auth, signed_in_resource=nil)
    user = User.where('uid = ?', auth.uid).first
    if user != nil && ((user.provider == nil && auth == "reador") || ((user.provider != nil) && (!user.provider.include? auth.provider)))
      user = nil
    end
    unless user
      user = User.create(provider:auth.provider,
                           uid:auth.uid,
                           password:Devise.friendly_token[0,20],
                           login:auth["info"]["nickname"],
                           )
    end
    user
  end
  
  def self.new_with_session(params, session)
    super.tap do |user|
      if data = session["devise.twitter_data"] && session["devise.twitter_data"]["extra"]["raw_info"]
        user.email = data["email"] if user.email.blank?
        user.login = session["devise.twitter_data"]["info"].nickname
        user.provider = session["devise.twitter_data"].provider
        user.uid = session["devise.twitter_data"].uid
        user.password = Devise.friendly_token[0,20]
        user.token = session["devise.twitter_data"].credentials.token
        user.tokenSecret = session["devise.twitter_data"].credentials.secret
        user.add_timeline_to_sources
      elsif data = session["devise.github_data"] && session["devise.github_data"]["extra"]["raw_info"]
        user.email = data["email"] if user.email.blank?
        user.provider = session["devise.github_data"].provider
        user.uid = session["devise.github_data"].uid
        user.password = Devise.friendly_token[0,20]
      elsif data = session["devise.google_data"] && session["devise.google_data"]["extra"]["raw_info"]
        user.email = data["email"] if user.email.blank?
        user.provider = session["devise.google_data"].provider
        user.uid = session["devise.google_data"].uid
        user.password = Devise.friendly_token[0,20]
      end
    end
  end


  def getSources
    return Source.all(
        "?concept <#{ZoneOntology::SOURCES_OWNER}> ?owner.
          Filter(str(?owner) = \"#{id}\")")
  end

  def add_timeline_to_sources
    @source = Source.new(
        "#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}/#{self.login}",
        {
            :owner => self.id,
            :attrs => {
                RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE,
                ZoneOntology::SOURCES_TWITTER_TOKEN =>self.token,
                ZoneOntology::SOURCES_TWITTER_TOKEN_SECRET => self.tokenSecret
            }
        }
    )
    @source.save
  end
end
