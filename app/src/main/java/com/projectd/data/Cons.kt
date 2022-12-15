package com.projectd.data

object Cons {
    object NOTIFICATION {
        const val ID_STARTUP = 890
        const val ID_REPORT = 891
    }

    object BUNDLE {
        const val DATA = "data"
        const val PRAYER_SEEK = "prayer_seek"
        const val PRAYER_COUNT = "prayer_count"
    }

    object DB {
        object CONFIG {
            const val REFF_NAME = "config"
            const val APP_CONFIG = "app_config"
            const val TOTAL_USER = "total_user"
            const val APP_VERSION = "app_version"
            const val BC_GROUP = "bc_group"
            const val BAN_MESSAGE = "ban_message"
        }

        object PRAYER {
            const val REFF_NAME = "prayer"
        }

        object USER {
            const val FCM_ID = "fcmId"
        }

        object PROJECT {
            const val REFF_NAME = "project"
            const val CREATED_AT = "createdAt"
            const val PROGRESSES = "progresses"
            const val END_DATE = "endDate"
        }

        object TASK {
            const val REFF_NAME = "task"
            const val CREATED_AT = "createdAt"
            const val STATUS = "status"
            const val DATE = "date"
        }

        object ABSENT {
            const val REFF_NAME = "absent"
            const val ID = "id"
            const val CREATED_AT = "createdAt"
            const val STATUS = "status"
            const val DATE = "date"
        }
    }

    object DIVISION {
        const val WEB = "web"
        const val MOBILE = "mobile"
        const val TESTER = "tester"
        const val ANALYST = "analyst"
        const val MARKETING = "marketing"
        const val PSDM = "psdm"
        const val SUPER_ADMIN = "super admin"
        const val MANAGER = "manager"
    }

    object PREVILLEGE {
        const val RTASK = "rtask"
        const val WTASK = "wtask"
        const val RPROJECT = "rproject"
        const val WPROJECT = "wproject"
        const val RINBOX = "rinbox"
        const val WINBOX = "winbox"
    }

    object URL {
        const val OVERTIME = "https://docs.google.com/forms/d/e/1FAIpQLSfPvNGNnltgb3etcrJpQfil5VXp1N1JjOdrAPmkY34FhlQB4A/viewform"
        const val LEAVE = "https://forms.gle/oro7xkQfxcKX3HR47"
        const val REIMBURSEMENT = "https://docs.google.com/forms/d/e/1FAIpQLSeKPA3eiRODCdDUPllEplObhS5urHvgJbGtP2fPkoyFqBf1qA/viewform"
    }
}