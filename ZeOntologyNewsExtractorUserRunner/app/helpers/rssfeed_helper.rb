module RssfeedHelper

  def make_big_and_green(target)
    page.call "new Effect.Scale", target, 200
    page.call "new Effect.Morph", target, :style=>'color:#00FF00;'
  end
end