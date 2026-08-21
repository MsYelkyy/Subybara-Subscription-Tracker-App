package com.subybara.app.navigation

sealed interface SubybaraDestination {
    val route: String

    data object Welcome : SubybaraDestination {
        override val route: String = "welcome"
    }

    data object SignIn : SubybaraDestination {
        override val route: String = "sign_in"
    }

    data object Home : SubybaraDestination {
        override val route: String = "home"
    }

    data object Subscriptions : SubybaraDestination {
        override val route: String = "subscriptions"
    }

    data object Renewals : SubybaraDestination {
        override val route: String = "renewals"
    }

    data object Insights : SubybaraDestination {
        override val route: String = "insights"
    }

    data object Profile : SubybaraDestination {
        override val route: String = "profile"
    }

    companion object {
        private val allDestinations = listOf(
            Welcome,
            SignIn,
            Home,
            Subscriptions,
            Renewals,
            Insights,
            Profile
        )

        fun fromRoute(route: String): SubybaraDestination =
            allDestinations.firstOrNull { destination -> destination.route == route } ?: Welcome
    }
}
