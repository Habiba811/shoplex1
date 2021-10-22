package com.alpha.shoplex.view.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.alpha.shoplex.BuildConfig
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.DialogAddReportBinding
import com.alpha.shoplex.databinding.FragmentAccountBinding
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.Report
import com.alpha.shoplex.view.activities.HomeActivity
import com.alpha.shoplex.view.activities.OrderActivity
import com.alpha.shoplex.view.activities.ProfileActivity
import com.alpha.shoplex.view.activities.auth.AuthActivity
import com.alpha.shoplex.viewmodel.AuthVM
import java.util.*

class AccountFragment : Fragment() {
    lateinit var binding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            if (UserInfo.userID == null) {
                startActivity(Intent(this.context, AuthActivity::class.java))
            } else {
                showDialog()
            }
        }

        binding.cardProfile.setOnClickListener {
            if(UserInfo.userID != null) {
                startActivity(Intent(context, ProfileActivity::class.java))
            } else{
                val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.pleaseLogin), Snackbar.LENGTH_LONG)
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                snackbar.show()
            }
        }

        binding.cardOrder.setOnClickListener {
            if(UserInfo.userID != null) {
                startActivity(Intent(context, OrderActivity::class.java))
            } else{
                val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.pleaseLogin), Snackbar.LENGTH_LONG)
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                snackbar.show()
            }
        }

        binding.cardShare.setOnClickListener {
            shareSuccess()
        }

        binding.cardLanguage.setOnClickListener {
            if(binding.tvLanguage.text.toString().contains("En", true)){
                requireContext().getSharedPreferences("LANG", AppCompatActivity.MODE_PRIVATE).edit().putString("Language", "en").apply()
                UserInfo.lang = "en"
                UserInfo.setLocale("en", requireActivity())
            }else{
                requireContext().getSharedPreferences("LANG", AppCompatActivity.MODE_PRIVATE).edit().putString("Language", "ar").apply()
                UserInfo.lang = "ar"
                UserInfo.setLocale("ar", requireActivity())
            }
        }

        binding.switchNotification.setOnClickListener {
            UserInfo.saveNotification(requireContext(), binding.switchNotification.isChecked)
        }

        binding.cardRate.setOnClickListener {
            rateSuccess()
        }

        binding.cardReport.setOnClickListener {
            showAddReportDialog()
        }

        return binding.root
    }

    private fun checkLogin() {
        if (UserInfo.userID == null) {
            binding.btnLogout.text = getString(R.string.login)
            binding.tvUserName.text = getText(R.string.fullAccess)
            binding.switchNotification.isEnabled = false
            binding.cardReport.visibility = View.INVISIBLE
        } else {
            binding.btnLogout.text = getString(R.string.logOut)
            binding.tvUserName.text = UserInfo.name
            binding.switchNotification.isEnabled = true
            binding.switchNotification.isChecked = UserInfo.readNotification(requireContext())
            binding.cardReport.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    fun showDialog() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle(getString(R.string.logOut))
        builder?.setMessage(getString(R.string.logoutMessage))

        builder?.setPositiveButton(getString(R.string.yes)) { _, _ ->
            AuthVM.logout(requireContext())
            checkLogin()
        }

        builder?.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }

        builder?.show()
    }

    private fun showAddReportDialog() {
        val dialogBinding = DialogAddReportBinding.inflate(layoutInflater)
        val reportBtnSheetDialog = BottomSheetDialog(dialogBinding.root.context,R.style.BottomSheetDialogTheme)

        dialogBinding.btnSendReport.setOnClickListener {
            val reportMsg = dialogBinding.edReport.text.toString()
            val report = Report(
                "Client",
                reportMsg, Timestamp.now().toDate()
            )
            FirebaseReferences.ReportRef.add(report)
            reportBtnSheetDialog.dismiss()
            val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.report), Snackbar.LENGTH_LONG)
            val sbView: View = snackbar.view
            sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
            snackbar.show()

        }
        reportBtnSheetDialog.setContentView(dialogBinding.root)
        reportBtnSheetDialog.show()

    }

    //share application
    private fun shareSuccess() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shoplex")
        var shareMessage = "\nLet me recommend you this application\n\n"
        shareMessage = """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                     """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "choose one"))
    }

    //rate application
    private fun rateSuccess() {
        val uri: Uri = Uri.parse("market://details?id=" + activity?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity?.packageName)
                )
            )
        }
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().createConfigurationContext(config)
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )
        val refresh = Intent(requireActivity(), HomeActivity::class.java)
        requireActivity().finish()
        startActivity(refresh)
    }
}