import React from 'react';
import { Procedure } from '../types.ts';
import { Language } from '../i18n/translations.ts';
import {
  Clock,
  Coins,
  ShieldCheck,
  ChevronRight,
  Building2,
  CheckCircle,
  Globe,
  AlertCircle,
  AlertTriangle,
  Users,
  Timer,
} from 'lucide-react';

interface ProcedureCardProps {
  procedure: Procedure;
  onSelect: (p: Procedure) => void;
  lang: Language;
  userProgress?: {
    checked: number;
    total: number;
    percentage: number;
  };
}

export const ProcedureCard: React.FC<ProcedureCardProps> = ({
  procedure,
  onSelect,
  lang,
  userProgress,
}) => {
  const getDifficultyBadge = (diff: string) => {
    switch (diff) {
      case 'usor':
        return (
          <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
            {lang === 'ro' ? 'Ușor' : 'Easy'}
          </span>
        );
      case 'mediu':
        return (
          <span className="text-[11px] font-semibold text-amber-700 bg-amber-50 px-2 py-0.5 rounded-full border border-amber-200">
            {lang === 'ro' ? 'Mediu' : 'Medium'}
          </span>
        );
      case 'complex':
        return (
          <span className="text-[11px] font-semibold text-rose-700 bg-rose-50 px-2 py-0.5 rounded-full border border-rose-200">
            {lang === 'ro' ? 'Complex' : 'Complex'}
          </span>
        );
      default:
        return null;
    }
  };

  const formatVerifiedDate = (dateStr: string) => {
    if (!dateStr) return lang === 'ro' ? 'Recent' : 'Recent';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString(lang === 'ro' ? 'ro-RO' : 'en-GB', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
      });
    } catch {
      return dateStr;
    }
  };

  const isNeedsReview = procedure.verification_status === 'needs_review';

  // Profile labels
  const getProfileBadge = () => {
    if (!procedure.target_profiles || procedure.target_profiles.length === 0) return null;
    if (procedure.target_profiles.includes('eu_eea_ch')) {
      return (
        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-blue-50 text-blue-700 border border-blue-200">
          EU / SEE / CH
        </span>
      );
    }
    if (procedure.target_profiles.includes('non_eu')) {
      return (
        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-purple-50 text-purple-700 border border-purple-200">
          Non-UE
        </span>
      );
    }
    if (procedure.target_profiles.includes('family_member')) {
      return (
        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-pink-50 text-pink-700 border border-pink-200">
          <Users className="w-2.5 h-2.5" />
          {lang === 'ro' ? 'Membru familie' : 'Family member'}
        </span>
      );
    }
    return null;
  };

  const displayTitle = lang === 'en' && procedure.title_en ? procedure.title_en : procedure.title;
  const displayDesc = lang === 'en' && procedure.short_description_en ? procedure.short_description_en : procedure.short_description;
  const displayAuthority = lang === 'en' && procedure.competent_authority_en ? procedure.competent_authority_en : procedure.competent_authority;
  const displayFee = lang === 'en' && procedure.total_fee_en ? procedure.total_fee_en : procedure.total_fee;
  const displayTime = lang === 'en' && procedure.estimated_time_en ? procedure.estimated_time_en : procedure.estimated_time;

  const deadlineApproachingInfo = React.useMemo(() => {
    const rawDate = procedure.deadline || procedure.expiry_date;
    if (!rawDate) return null;
    try {
      const targetDate = new Date(rawDate);
      if (isNaN(targetDate.getTime())) return null;
      const today = new Date();
      const targetMidnight = new Date(targetDate.getFullYear(), targetDate.getMonth(), targetDate.getDate()).getTime();
      const todayMidnight = new Date(today.getFullYear(), today.getMonth(), today.getDate()).getTime();
      const diffDays = Math.ceil((targetMidnight - todayMidnight) / (1000 * 60 * 60 * 24));
      if (diffDays >= 0 && diffDays <= 30) {
        return {
          daysRemaining: diffDays,
          formattedDate: targetDate.toLocaleDateString(lang === 'ro' ? 'ro-RO' : 'en-GB', {
            day: 'numeric',
            month: 'short',
          }),
        };
      }
    } catch {
      return null;
    }
    return null;
  }, [procedure.deadline, procedure.expiry_date, lang]);

  return (
    <div
      onClick={() => onSelect(procedure)}
      className="group relative bg-white rounded-2xl border border-slate-200/90 hover:border-blue-400 p-5 sm:p-6 transition-all duration-200 hover:shadow-xl hover:shadow-blue-900/5 cursor-pointer flex flex-col justify-between"
    >
      <div>
        {/* Top badges */}
        <div className="flex items-center justify-between gap-2 mb-3">
          <div className="flex items-center gap-1.5 flex-wrap">
            {procedure.is_popular && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-bold bg-blue-600 text-white shadow-2xs">
                {lang === 'ro' ? 'Popular' : 'Popular'}
              </span>
            )}
            {deadlineApproachingInfo && (
              <span
                className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-bold bg-amber-100 text-amber-900 border border-amber-300 shadow-2xs"
                title={lang === 'ro' ? `Termen limită pe ${deadlineApproachingInfo.formattedDate} (${deadlineApproachingInfo.daysRemaining} zile rămase)` : `Deadline: ${deadlineApproachingInfo.formattedDate} (${deadlineApproachingInfo.daysRemaining} days remaining)`}
              >
                <AlertTriangle className="w-3 h-3 text-amber-700" />
                <span>
                  {lang === 'ro'
                    ? `Termen: ${deadlineApproachingInfo.daysRemaining}z`
                    : `Deadline: ${deadlineApproachingInfo.daysRemaining}d`}
                </span>
              </span>
            )}
            {procedure.is_online_available && (
              <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200">
                <Globe className="w-3 h-3" />
                <span>{lang === 'ro' ? 'Online' : 'Online'}</span>
              </span>
            )}
            {procedure.estimated_time_minutes != null && (
              <span
                className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-semibold bg-cyan-50 text-cyan-800 border border-cyan-200 shadow-2xs"
                title={
                  lang === 'ro'
                    ? `Timp estimat de parcurgere / completare: ~${procedure.estimated_time_minutes} minute`
                    : `Estimated completion time: ~${procedure.estimated_time_minutes} minutes`
                }
                data-testid="procedure-card-estimated-time"
              >
                <Timer className="w-3 h-3 text-cyan-600" />
                <span>~{procedure.estimated_time_minutes} min</span>
              </span>
            )}
            {getProfileBadge()}
            {lang === 'en' && !procedure.title_en && (
              <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-50 text-amber-800 border border-amber-200" title="Available only in Romanian - English translation in editorial review">
                RO only
              </span>
            )}
            <span className="text-[11px] font-medium text-slate-500 uppercase tracking-wider">
              {procedure.category}
            </span>
          </div>

          <div className="flex items-center gap-1.5">
            {getDifficultyBadge(procedure.difficulty)}
          </div>
        </div>

        {/* Title & Description */}
        <h3 className="text-lg font-bold text-slate-900 group-hover:text-blue-700 transition leading-snug">
          {displayTitle}
        </h3>

        {/* Romanian legal designation notice if viewing in English */}
        {lang === 'en' && procedure.title_en && (
          <p className="mt-1 text-xs text-slate-500 italic">
            Official Romanian Title: <span className="font-medium text-slate-700 not-italic">{procedure.title}</span>
          </p>
        )}
        
        <p className="mt-2 text-xs sm:text-sm text-slate-600 line-clamp-2 leading-relaxed">
          {displayDesc}
        </p>

        {/* Authority note */}
        <div className="mt-3 flex items-center gap-1.5 text-xs text-slate-500">
          <Building2 className="w-3.5 h-3.5 text-slate-400 shrink-0" />
          <span className="truncate font-medium">{displayAuthority}</span>
        </div>

        {/* Key Quick Facts Grid */}
        <div className="mt-4 pt-3 border-t border-slate-100 grid grid-cols-2 gap-2 text-xs text-slate-700">
          <div className="flex items-center gap-1.5">
            <Coins className="w-3.5 h-3.5 text-amber-600 shrink-0" />
            <span className="truncate font-semibold">{displayFee}</span>
          </div>
          <div className="flex items-center gap-1.5">
            <Clock className="w-3.5 h-3.5 text-blue-600 shrink-0" />
            <span className="truncate">{displayTime}</span>
          </div>
        </div>
      </div>

      {/* Card Footer: Checklist progress or verification note */}
      <div className="mt-5 pt-3.5 border-t border-slate-100 flex items-center justify-between text-xs">
        {userProgress && userProgress.checked > 0 ? (
          <div className="flex items-center gap-2">
            <div className="flex items-center gap-1 font-semibold text-blue-700">
              <CheckCircle className="w-3.5 h-3.5 text-blue-600" />
              <span>
                {userProgress.checked}/{userProgress.total} {lang === 'ro' ? 'acte bifate' : 'docs checked'}
              </span>
            </div>
            <div className="w-16 bg-slate-100 rounded-full h-1.5 overflow-hidden">
              <div
                className="bg-blue-600 h-1.5 rounded-full transition-all duration-500 ease-out"
                style={{ width: `${userProgress.percentage}%` }}
              />
            </div>
          </div>
        ) : (
          <div className="flex items-center gap-1.5 text-slate-500">
            {isNeedsReview ? (
              <span className="inline-flex items-center gap-1 text-amber-700 font-medium">
                <AlertCircle className="w-3.5 h-3.5 text-amber-500" />
                <span>{lang === 'ro' ? 'De verificat' : 'Needs review'}</span>
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 text-emerald-700 font-medium">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
                <span>
                  {lang === 'ro' ? 'Verificat' : 'Verified'} {formatVerifiedDate(procedure.last_verified_at)}
                </span>
              </span>
            )}
          </div>
        )}

        <div className="flex items-center gap-1 font-semibold text-blue-700 group-hover:translate-x-0.5 transition-transform">
          <span>{lang === 'ro' ? 'Vezi ghidul' : 'View guide'}</span>
          <ChevronRight className="w-4 h-4" />
        </div>
      </div>
    </div>
  );
};
