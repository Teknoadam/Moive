package com.lagradost.cloudstream3.plugins

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class BelgeselX : MainAPI() {
    override var mainUrl = "https://belgeselx.com"
    override var name = "BelgeselX"
    override val hasMainPage = true
    override var lang = "tr"
    override val hasQuickSearch = false
    override val hasChromecastSupport = true
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.Documentary)

    private val categories = mapOf(
        "${mainUrl}/konu/turk-tarihi-belgeselleri&page=" to "Türk Tarihi",
        "${mainUrl}/konu/tarih-belgeselleri&page=" to "Tarih",
        "${mainUrl}/konu/seyehat-belgeselleri&page=" to "Seyahat",
        "${mainUrl}/konu/seri-belgeseller&page=" to "Seri",
        "${mainUrl}/konu/savas-belgeselleri&page=" to "Savaş",
        "${mainUrl}/konu/sanat-belgeselleri&page=" to "Sanat",
        "${mainUrl}/konu/psikoloji-belgeselleri&page=" to "Psikoloji",
        "${mainUrl}/konu/polisiye-belgeselleri&page=" to "Polisiye",
        "${mainUrl}/konu/otomobil-belgeselleri&page=" to "Otomobil",
        "${mainUrl}/konu/nazi-belgeselleri&page=" to "Nazi",
        "${mainUrl}/konu/muhendislik-belgeselleri&page=" to "Mühendislik",
        "${mainUrl}/konu/kultur-din-belgeselleri&page=" to "Kültür Din",
        "${mainUrl}/konu/kozmik-belgeseller&page=" to "Kozmik",
        "${mainUrl}/konu/hayvan-belgeselleri&page=" to "Hayvan",
        "${mainUrl}/konu/eski-tarih-belgeselleri&page=" to "Eski Tarih",
        "${mainUrl}/konu/egitim-belgeselleri&page=" to "Eğitim",
        "${mainUrl}/konu/dunya-belgeselleri&page=" to "Dünya",
        "${mainUrl}/konu/doga-belgeselleri&page=" to "Doğa",
        "${mainUrl}/konu/bilim-belgeselleri&page=" to "Bilim"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val items = categories.map { (url, name) ->
            val data = app.get(url + page).document.select("div.video-container a").map {
                val title = it.selectFirst("h3")?.text().toString()
                val link = it.attr("href")
                val img = it.selectFirst("img")?.attr("src").toString()
                MovieSearchResponse(
                    name = title,
                    url = link,
                    apiName = this.name,
                    posterUrl = img,
                    type = TvType.Documentary
                )
            }
            HomePageList(name, data)
        }
        return HomePageResponse(items)
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1.title")?.text().toString()
        val description = doc.selectFirst("div.description")?.text().toString()
        val posterUrl = doc.selectFirst("img.poster")?.attr("src").toString()

        val episodes = doc.select("a.episode-link").map {
            Episode(
                data = it.attr("href"),
                name = it.text(),
            )
        }

        return TvSeriesLoadResponse(
            name = title,
            url = url,
            apiName = this.name,
            type = TvType.Documentary,
            posterUrl = posterUrl,
            plot = description,
            episodes = episodes
        )
    }
}
